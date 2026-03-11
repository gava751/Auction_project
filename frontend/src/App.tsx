import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { HomePage } from './pages/HomePage';
import { LoginPage } from './pages/LoginPage';
import { LotPage } from './pages/LotPage';
import { useAuthStore } from './store/useAuthStore';
import {OAuth2Redirect} from "./pages/OAuth2Redirect.tsx";

function App() {
    // Достаем состояние пользователя из хранилища Zustand
    const { user, logout } = useAuthStore();

    return (
        <BrowserRouter>
            <div className="min-h-screen bg-gray-50 text-gray-900">

                {/* Навигационная панель */}
                <nav className="bg-white shadow-sm p-4 mb-6">
                    <div className="container mx-auto flex justify-between items-center">
                        <Link to="/" className="font-bold text-blue-600 text-xl tracking-tight">
                            AuctionPlatform
                        </Link>

                        <div>
                            {user ? (
                                <div className="flex items-center gap-4">
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

                {/* Контент страниц */}
                <main className="container mx-auto px-4">
                    <Routes>
                        <Route path="/" element={<HomePage />} />
                        <Route path="/login" element={<LoginPage />} />
                        <Route path="/lots/:id" element={<LotPage />} />
                        <Route path="/oauth2/callback" element={<OAuth2Redirect />} />
                    </Routes>
                </main>

            </div>
        </BrowserRouter>
    );
}

export default App;