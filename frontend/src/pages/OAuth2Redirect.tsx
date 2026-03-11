import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

export const OAuth2Redirect = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const login = useAuthStore(state => state.login);

    useEffect(() => {
        const token = searchParams.get('token');
        if (token) {
            login({ email: 'google-user', role: 'ROLE_BUYER' }, token);
            navigate('/');
        }
    }, [searchParams, login, navigate]);

    return <div>Авторизация...</div>;
};