import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

const parseJwt = (token: string) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
};

export const OAuth2Redirect = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const login = useAuthStore(state => state.login);

    useEffect(() => {
        const token = searchParams.get('token');

        if (token) {
            const decodedPayload = parseJwt(token);

            const email = decodedPayload?.sub || 'Google User';

            const nickname = email.includes('@') ? email.split('@')[0] : email;

            login({ email: nickname, role: 'ROLE_BUYER' }, token);
            navigate('/');
        } else {
            navigate('/login');
        }
    }, [searchParams, login, navigate]);

    return (
        <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <span className="ml-4 font-medium text-gray-600">Авторизация...</span>
        </div>
    );
};