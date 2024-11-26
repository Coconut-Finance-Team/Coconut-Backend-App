import React, { useState, useEffect, useRef } from 'react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import styled from 'styled-components';

const Container = styled.div`
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
  background: #ffffff;
`;

const Header = styled.div`
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
`;

const MarketName = styled.div`
  font-size: 28px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
`;

const PriceInfo = styled.div`
  display: flex;
  align-items: baseline;
  gap: 12px;
`;

const CurrentPrice = styled.div`
  font-size: 28px;
  font-weight: 600;
  color: #333;
`;

const Change = styled.div`
  font-size: 16px;
  color: ${props => (props.value > 0 ? '#ff4747' : '#4788ff')};
`;

const TimeframeButtons = styled.div`
  display: flex;
  gap: 12px;
  margin-bottom: 40px;
`;

const TimeButton = styled.button`
  padding: 8px 16px;
  border: none;
  background: ${props => (props.active ? '#f1f3f5' : 'transparent')};
  color: ${props => (props.active ? '#333' : '#999')};
  font-size: 14px;
  font-weight: ${props => (props.active ? '600' : '400')};
  cursor: pointer;
  border-radius: 8px;

  &:hover {
    background: #f1f3f5;
  }
`;

const ChartContainer = styled.div`
  width: 100%;
  height: 500px;
  background: #ffffff;
  position: relative;
  overflow-x: scroll;
  overflow-y: hidden;
  
  /* 스크롤바 스타일링 */
  ::-webkit-scrollbar {
    height: 8px;
  }
  
  ::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
  }
  
  ::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
  }
  
  ::-webkit-scrollbar-thumb:hover {
    background: #555;
  }
`;

const ChartContent = styled.div`
  min-width: 1500px;
  width: max-content;
  height: 100%;
`;

const CustomTooltipContainer = styled.div`
  background: rgba(0, 0, 0, 0.8);
  border-radius: 4px;
  padding: 12px;
  color: white;
`;

const TooltipLabel = styled.div`
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
`;

const TooltipValue = styled.div`
  font-size: 14px;
  font-weight: 600;
`;

function KospiChart() {
  const [chartData, setChartData] = useState([]);
  const [marketData, setMarketData] = useState({
    value: 0,
    change: 0,
    changePercent: 0
  });
  const [isLoading, setIsLoading] = useState(true);
  const wsRef = useRef(null);
  const lastReceivedData = useRef(null);
  const chartRef = useRef(null);

  // 장 마감 시간 체크
  const isMarketClosed = () => {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    return hours >= 15 && minutes >= 20;
  };

  // 과거 데이터 로드
  useEffect(() => {
    const fetchHistoricalData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/v1/stock/1001/charts/1min');
        const data = await response.json();
        
        const formattedData = data.map(item => ({
          time: new Date(item.time).toLocaleTimeString('ko-KR', {
            hour: '2-digit',
            minute: '2-digit'
          }),
          value: parseFloat(item.currentPrice)
        })).sort((a, b) => {
          const timeA = a.time.split(':').map(Number);
          const timeB = b.time.split(':').map(Number);
          return (timeA[0] * 60 + timeA[1]) - (timeB[0] * 60 + timeB[1]);
        });

        if (formattedData.length > 0) {
          setChartData(formattedData);
          
          const lastPrice = formattedData[formattedData.length - 1].value;
          const prevPrice = formattedData[formattedData.length - 2]?.value;

          lastReceivedData.current = {
            time: formattedData[formattedData.length - 1].time,
            value: lastPrice
          };

          if (prevPrice) {
            const change = lastPrice - prevPrice;
            const changePercent = (change / prevPrice) * 100;
            setMarketData({
              value: lastPrice,
              change,
              changePercent
            });
          }

          // 차트 스크롤을 가장 오른쪽으로 이동
          setTimeout(() => {
            if (chartRef.current) {
              chartRef.current.scrollLeft = chartRef.current.scrollWidth;
            }
          }, 100);
        }
        
        setIsLoading(false);
      } catch (error) {
        console.error('Error fetching historical data:', error);
        setIsLoading(false);
      }
    };

    fetchHistoricalData();
  }, []);

  // WebSocket 연결
  useEffect(() => {
    if (isLoading || isMarketClosed()) return;

    const connectWebSocket = () => {
      wsRef.current = new WebSocket('ws://localhost:8080/ws/stock/0001');
      
      wsRef.current.onopen = () => {
        console.log('WebSocket Connected');
      };

      wsRef.current.onmessage = (event) => {
        try {
          const newData = JSON.parse(event.data);
          const timeStr = newData.time;
          const formattedTime = `${timeStr.slice(0, 2)}:${timeStr.slice(2, 4)}`;
          const currentPrice = parseFloat(newData.currentPrice);

          setChartData(prevData => {
            const newPoint = {
              time: formattedTime,
              value: currentPrice
            };

            if (lastReceivedData.current) {
              const change = currentPrice - lastReceivedData.current.value;
              const changePercent = (change / lastReceivedData.current.value) * 100;

              setMarketData({
                value: currentPrice,
                change,
                changePercent
              });
            }

            lastReceivedData.current = {
              time: formattedTime,
              value: currentPrice
            };

            const updatedData = [...prevData];
            if (updatedData.length > 0 && updatedData[updatedData.length - 1].time === formattedTime) {
              updatedData[updatedData.length - 1] = newPoint;
            } else {
              updatedData.push(newPoint);
            }

            if (chartRef.current) {
              chartRef.current.scrollLeft = chartRef.current.scrollWidth;
            }

            return updatedData;
          });
        } catch (error) {
          console.error('Error processing WebSocket data:', error);
        }
      };

      wsRef.current.onclose = () => {
        if (!isMarketClosed()) {
          console.log('WebSocket Disconnected, attempting to reconnect...');
          setTimeout(connectWebSocket, 3000);
        } else {
          console.log('Market closed, stopping reconnection attempts');
        }
      };

      wsRef.current.onerror = (error) => {
        console.error('WebSocket Error:', error);
      };
    };

    connectWebSocket();

    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, [isLoading]);

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      const currentValue = payload[0].value;
      const baseValue = lastReceivedData.current?.value;
      const change = baseValue ? ((currentValue - baseValue) / baseValue) * 100 : 0;

      return (
        <CustomTooltipContainer>
          <TooltipLabel>{payload[0].payload.time}</TooltipLabel>
          <TooltipValue>
            {currentValue.toFixed(2)}
            <div style={{ fontSize: '12px', marginTop: '4px', color: '#999' }}>
              {change.toFixed(2)}%
            </div>
          </TooltipValue>
        </CustomTooltipContainer>
      );
    }
    return null;
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <Container>
      <Header>
        <div>
          <MarketName>코스닥</MarketName>
          <PriceInfo>
            <CurrentPrice>
              {marketData.value.toFixed(2)}
            </CurrentPrice>
            <Change value={marketData.change}>
              {marketData.change > 0 ? '+' : ''}
              {marketData.change.toFixed(2)} ({marketData.changePercent.toFixed(2)}%)
            </Change>
          </PriceInfo>
        </div>
      </Header>

      <TimeframeButtons>
        <TimeButton active={true}>실시간</TimeButton>
      </TimeframeButtons>

      <ChartContainer ref={chartRef}>
        <ChartContent>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart 
              data={chartData}
              margin={{ top: 20, right: 40, left: 0, bottom: 20 }}
            >
              <CartesianGrid 
                strokeDasharray="3 3" 
                stroke="rgba(240, 240, 240, 0.8)" 
                opacity={0.5}
              />
              <XAxis 
                dataKey="time" 
                tick={{ fontSize: 12, fill: '#8B95A1' }}
                interval={Math.floor(chartData.length / 15)}
                axisLine={false}
                tickLine={false}
                padding={{ left: 10, right: 10 }}
              />
              <YAxis 
                domain={['auto', 'auto']}
                tick={{ fontSize: 12, fill: '#8B95A1' }}
                orientation="right"
                axisLine={false}
                tickLine={false}
                width={60}
                padding={{ top: 30, bottom: 30 }}
              />
              <Tooltip 
                content={<CustomTooltip />}
                cursor={{ 
                  stroke: '#666', 
                  strokeDasharray: '5 5', 
                  strokeWidth: 1 
                }}
              />
              <Line
                type="monotoneX"
                dataKey="value"
                stroke="#ff4747"
                strokeWidth={1.5}
                dot={false}
                isAnimationActive={false}
                connectNulls={true}
              />
            </LineChart>
          </ResponsiveContainer>
        </ChartContent>
      </ChartContainer>
    </Container>
  );
}

export default KospiChart;