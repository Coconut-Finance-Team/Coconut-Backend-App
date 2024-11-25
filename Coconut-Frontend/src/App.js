import React, { useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import styled, { createGlobalStyle } from 'styled-components';
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import Homeboard from './components/Homeboard';
import Account from './components/Account';
import Subscription from './components/subscription/Subscription';
import SubscriptionApply from './components/subscription/SubscriptionApply';
import SubscriptionConfirm from './components/subscription/SubscriptionConfirm';
import SubscriptionComplete from './components/subscription/SubscriptionComplete';
import SubscriptionInquiry from './components/subscription/SubscriptionInquiry';
import Login from './components/auth/Login';
import Signin from './components/auth/Signin';
import SigninUserInfo from './components/auth/SigninUserInfo';
import SigninAddInfo from './components/auth/SigninAddInfo';
import FindIdPassword from './components/auth/FindIdPassword';
import RealTimeChart from './components/home/RealTimeChart';
import StockDetail from './components/home/StockDetail';
import KospiChart from './components/home/KospiChart';
import KosdaqChart from './components/home/KosdaqChart';
import SearchPage from './components/common/SearchPage';
import AdminPage from './components/admin/AdminPage';
import MyPage from './components/mypage';

const GlobalStyle = createGlobalStyle`
  body {
    font-family: 'Noto Sans KR', sans-serif;
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
`;

function App() {
  const [user, setUser] = useState(null);
  const isAdminPath = window.location.pathname.startsWith('/admin');

  if (isAdminPath) {
    return (
      <>
        <GlobalStyle />
        <Routes>
          <Route path="/admin/*" element={<AdminPage />} />
        </Routes>
      </>
    );
  }

  return (
    <>
      <GlobalStyle />
      <div>
        <Header user={user} setUser={setUser} />
        <main>
          <Routes>
            <Route path="/" element={<Homeboard />} />
            <Route path="/account/*" element={<Account />} />
            <Route path="/subscription" element={<Navigate to="/subscription/table" replace />} />
            <Route path="/subscription/*" element={<Subscription />} />
            <Route path="/subscription/apply/:id" element={<SubscriptionApply />} />
            <Route path="/subscription/apply/confirm" element={<SubscriptionConfirm />} />
            <Route path="/subscription/apply/complete" element={<SubscriptionComplete />} />
            <Route path="/subscription/inquiry" element={<SubscriptionInquiry />} />
            <Route path="/login" element={<Login setUser={setUser} />} />
            <Route path="/signin" element={<Signin />} />
            <Route path="/signup/userinfo" element={<SigninUserInfo />} />
            <Route path="/signup/signinaddinfo" element={<SigninAddInfo />} />
            <Route path="/findidpassword" element={<FindIdPassword />} />
            <Route path="/chart" element={<RealTimeChart />} />
            <Route path="/stock/:stockId" element={<StockDetail />} />
            <Route path="/chart/kospi" element={<KospiChart />} />
            <Route path="/chart/kosdaq" element={<KosdaqChart />} />
            <Route path="/search" element={<SearchPage />} />
            <Route path="/mypage" element={<MyPage user={user} />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </>
  );
}

export default App;