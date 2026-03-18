import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { LotPage } from './pages/LotPage';
import { OAuth2Redirect } from './pages/OAuth2Redirect';
import { Dashboard } from './pages/Dashboard'; // Исправлено: добавили импорт
import { useAuthStore } from './store/useAuthStore';
import { CreateLotPage } from './pages/CreateLotPage';
import { ApplySellerPage } from './pages/ApplySellerPage';
import { WonLotsPage } from './pages/WonLotsPage';
function App() {
    const { user, logout } = useAuthStore();

    return (
        <BrowserRouter>
            <div className="min-h-screen bg-gray-50 text-gray-900">
                <nav className="bg-white shadow-sm p-4 mb-6">
                    <div className="container mx-auto flex justify-between items-center">
                        <Link to="/" className="font-bold text-blue-600 text-xl tracking-tight">
                            AuctionPlatform
                        </Link>

                        <div>
                            {user ? (
                                <div className="flex items-center gap-4">
                                    {user?.role === 'ROLE_ADMIN' && (
                                        <Link to="/admin" className="text-sm font-bold text-red-600 border border-red-200 px-3 py-1 rounded-lg hover:bg-red-50">
                                            Админка
                                        </Link>
                                    )}

                                    {user?.role === 'ROLE_SELLER' && (
                                        <>
                                            <Link to="/admin" className="text-sm font-bold text-green-600 border border-green-200 px-3 py-1 rounded-lg hover:bg-green-50">
                                                Мои лоты
                                            </Link>
                                        </>
                                    )}
                                    {user?.role === 'ROLE_BUYER' && (
                                        <Link to="/apply-seller" className="text-sm font-bold text-blue-600 border border-blue-200 px-3 py-1 rounded-lg hover:bg-blue-50">
                                            Стать продавцом
                                        </Link>
                                    )}
                                    {user?.role === 'ROLE_BUYER' && (
                                        <Link to="/my-wins" className="text-sm font-bold text-yellow-600 hover:text-yellow-700 flex items-center gap-1">
                                            🏆 Мои победы
                                        </Link>
                                    )}
                                    <span className="text-sm text-gray-600">Привет, {user.email}</span>
                                    <button
                                        onClick={logout}
                                        className="text-sm bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded transition"
                                    >
                                        Выйти
                                    </button>
                                </div>
                            ) : (
                                <Link
                                    to="/login"
                                    className="text-sm bg-blue-600 text-white hover:bg-blue-700 px-4 py-2 rounded transition"
                                >
                                    Войти
                                </Link>
                            )}
                        </div>
                    </div>
                </nav>

                <main className="container mx-auto px-4">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/lots/:id" element={<LotPage />} />
                        <Route path="/oauth2/callback" element={<OAuth2Redirect />} />
                        <Route path="/admin" element={<Dashboard />} />
                        <Route path="/create-lot" element={<CreateLotPage />} />
                        <Route path="/apply-seller" element={<ApplySellerPage />} />
                        <Route path="/my-wins" element={<WonLotsPage />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;