import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import api from '../api/axios';
import { useAuthStore } from '../store/useAuthStore';
import { Link } from 'react-router-dom';
import { Users, FileCheck, FolderTree, Ban, CheckCircle, Trash2, Plus, Gavel, ShieldPlus } from 'lucide-react';

interface SellerApplication { id: number; user: { email: string }; passportData: string; documentPath: string; }
interface User { id: number; email: string; role: string; status: string; }
interface Category { id: number; name: string; }
interface Lot { id: number; title: string; currentPrice: number; status: string; }

export const Dashboard = () => {
    const { user } = useAuthStore();
    const [activeTab, setActiveTab] = useState<'kyc' | 'users' | 'categories' | 'lots' | 'admins'>('kyc');
    const [myLots, setMyLots] = useState<Lot[]>([]);
    const [applications, setApplications] = useState<SellerApplication[]>([]);
    const [users, setUsers] = useState<User[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [lots, setLots] = useState<Lot[]>([]);
    const [newCatName, setNewCatName] = useState('');

    const [adminEmail, setAdminEmail] = useState('');
    const [adminPass, setAdminPass] = useState('');

    const loadData = useCallback(() => {
        if (activeTab === 'kyc') api.get<SellerApplication[]>('/admin/applications').then(res => setApplications(res.data));
        if (activeTab === 'users') api.get<User[]>('/admin/users').then(res => setUsers(res.data));
        if (activeTab === 'categories') api.get<Category[]>('/categories').then(res => setCategories(res.data));
        if (activeTab === 'lots') api.get<Lot[]>('/admin/lots').then(res => setLots(res.data));
    }, [activeTab]);

    useEffect(() => {
        if (user?.role === 'ROLE_ADMIN') {
            loadData();
        }
    }, [user, loadData]);
    useEffect(() => {
        if (user?.role === 'ROLE_SELLER') {
            api.get<Lot[]>('/lots/my').then(res => setMyLots(res.data));
        }
    }, [user]);
    const handleApprove = async (id: number) => {
        await api.post(`/admin/applications/${id}/approve`);
        loadData();
    };

    const toggleBlock = async (id: number, currentStatus: string) => {
        const action = currentStatus === 'ACTIVE' ? 'block' : 'unblock';
        await api.patch(`/admin/users/${id}/${action}`);
        loadData();
    };

    const addCategory = async () => {
        if (!newCatName) return;
        await api.post('/admin/categories', { name: newCatName });
        setNewCatName('');
        loadData();
    };

    const deleteCategory = async (id: number) => {
        await api.delete(`/admin/categories/${id}`);
        loadData();
    };

    const deleteLot = async (id: number) => {
        if (window.confirm("Удалить этот лот?")) {
            await api.delete(`/admin/lots/${id}`);
            loadData();
        }
    };

    const createAdmin = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            await api.post('/admin/users/admin', { email: adminEmail, password: adminPass });
            alert('Администратор создан!');
            setAdminEmail('');
            setAdminPass('');
            loadData();
        } catch (err: unknown) {
            if (axios.isAxiosError(err)) {
                alert(err.response?.data || 'Ошибка создания админа');
            }
        }
    };

    if (user?.role !== 'ROLE_ADMIN') {
        return (
            <div className="p-8 max-w-4xl mx-auto text-center">
                <CheckCircle size={48} className="mx-auto text-green-500 mb-4" />
                <h1 className="text-3xl font-bold mb-6">Кабинет Продавца</h1>
                <p className="text-gray-600 mb-8">Вы прошли верификацию и можете управлять своими аукционами.</p>
                <Link to="/create-lot" className="bg-green-600 text-white px-8 py-3 rounded-xl font-bold hover:bg-green-700 transition shadow-lg inline-block">
                    + Выставить новый лот
                </Link>
                <div className="mt-8 space-y-4 text-left max-w-2xl mx-auto">
                    <h3 className="font-bold text-xl mb-4 border-b pb-2">Ваши активные лоты</h3>

                    {myLots.map(l => (
                        <div key={l.id} className="p-4 border border-gray-200 rounded-xl flex justify-between items-center bg-white shadow-sm hover:shadow transition">
                            <div className="flex flex-col">
                                <Link
                                    to={`/lots/${l.id}`}
                                    className="font-bold text-gray-800 hover:text-blue-600 transition underline-offset-2 hover:underline"
                                >
                                    {l.title}
                                </Link>
                                <p className="text-sm text-blue-600 font-bold">${l.currentPrice.toFixed(2)}</p>
                            </div>
                                <button
                                    onClick={async () => {
                                        if(window.confirm("Вы уверены, что хотите удалить этот лот?")) {
                                            try {
                                                await api.delete(`/lots/${l.id}`);
                                                setMyLots(myLots.filter(x => x.id !== l.id));
                                            } catch {
                                                alert("Ошибка при удалении лота");
                                            }
                                        }
                                    }}
                                    className="text-red-500 p-2 hover:bg-red-50 rounded-lg transition"
                                    title="Удалить лот"
                                >
                                    <Trash2 size={20} />
                                </button>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    return (
        <div className="p-8 max-w-6xl mx-auto mb-20 text-left">
            <h1 className="text-4xl font-black mb-8 text-gray-900">Администрирование</h1>

            <div className="flex flex-wrap gap-2 mb-8 bg-gray-100 p-1 rounded-xl w-fit">
                <button onClick={() => setActiveTab('kyc')} className={`flex items-center gap-2 px-4 py-2 rounded-lg font-bold transition ${activeTab === 'kyc' ? 'bg-white shadow text-blue-600' : 'text-gray-500 hover:text-gray-700'}`}><FileCheck size={18}/>Заявки</button>
                <button onClick={() => setActiveTab('users')} className={`flex items-center gap-2 px-4 py-2 rounded-lg font-bold transition ${activeTab === 'users' ? 'bg-white shadow text-blue-600' : 'text-gray-500 hover:text-gray-700'}`}><Users size={18}/>Юзеры</button>
                <button onClick={() => setActiveTab('lots')} className={`flex items-center gap-2 px-4 py-2 rounded-lg font-bold transition ${activeTab === 'lots' ? 'bg-white shadow text-blue-600' : 'text-gray-500 hover:text-gray-700'}`}><Gavel size={18}/>Лоты</button>
                <button onClick={() => setActiveTab('categories')} className={`flex items-center gap-2 px-4 py-2 rounded-lg font-bold transition ${activeTab === 'categories' ? 'bg-white shadow text-blue-600' : 'text-gray-500 hover:text-gray-700'}`}><FolderTree size={18}/>Категории</button>
                <button onClick={() => setActiveTab('admins')} className={`flex items-center gap-2 px-4 py-2 rounded-lg font-bold transition ${activeTab === 'admins' ? 'bg-white shadow text-red-600' : 'text-gray-500 hover:text-gray-700'}`}><ShieldPlus size={18}/>Новый админ</button>
            </div>

            {activeTab === 'kyc' && (
                <div className="grid gap-4">
                    {applications.length === 0 && <p className="text-gray-400 italic">Новых заявок на верификацию нет</p>}
                    {applications.map(app => (
                        <div key={app.id} className="bg-white p-5 rounded-xl shadow-sm border flex justify-between items-center">
                            <div>
                                <p className="font-bold text-lg">{app.user.email}</p>
                                <p className="text-sm text-gray-500">Паспорт: {app.passportData}</p>
                                <a href={`http://localhost:8080/${app.documentPath}`} target="_blank" rel="noreferrer" className="text-blue-600 text-xs font-bold underline">Просмотреть документ</a>
                            </div>
                            <button onClick={() => handleApprove(app.id)} className="bg-green-100 text-green-700 px-4 py-2 rounded-lg font-bold hover:bg-green-200 transition">Одобрить</button>
                        </div>
                    ))}
                </div>
            )}

            {activeTab === 'users' && (
                <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b">
                        <tr><th className="p-4">Email</th><th className="p-4">Роль</th><th className="p-4">Статус</th><th className="p-4">Действие</th></tr>
                        </thead>
                        <tbody>
                        {users.map(u => (
                            <tr key={u.id} className="border-b last:border-0 hover:bg-gray-50">
                                <td className="p-4 font-medium">{u.email}</td>
                                <td className="p-4"><span className="text-xs font-bold px-2 py-1 bg-gray-100 rounded text-gray-600">{u.role}</span></td>
                                <td className="p-4">
                                    <span className={`text-xs font-bold ${u.status === 'ACTIVE' ? 'text-green-600' : 'text-red-600'}`}>{u.status}</span>
                                </td>
                                <td className="p-4">
                                    <button
                                        onClick={() => toggleBlock(u.id, u.status)}
                                        className={`p-2 rounded-lg transition ${u.status === 'ACTIVE' ? 'text-red-600 hover:bg-red-50' : 'text-green-600 hover:bg-green-50'}`}
                                        title={u.status === 'ACTIVE' ? 'Заблокировать' : 'Разблокировать'}
                                    >
                                        <Ban size={20} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === 'lots' && (
                <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b">
                        <tr><th className="p-4">Название</th><th className="p-4">Цена</th><th className="p-4">Статус</th><th className="p-4">Действие</th></tr>
                        </thead>
                        <tbody>
                        {lots.map(l => (
                            <tr key={l.id} className="border-b last:border-0 hover:bg-gray-50">
                                <td className="p-4 font-bold">{l.title}</td>
                                <td className="p-4 text-blue-600 font-semibold">${l.currentPrice}</td>
                                <td className="p-4"><span className="text-xs px-2 py-1 bg-gray-100 rounded text-gray-500 font-bold">{l.status}</span></td>
                                <td className="p-4">
                                    <button onClick={() => deleteLot(l.id)} className="text-red-500 hover:bg-red-50 p-2 rounded-lg transition"><Trash2 size={20}/></button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === 'categories' && (
                <div className="max-w-2xl">
                    <div className="flex gap-2 mb-6 text-left">
                        <input
                            type="text"
                            className="flex-1 p-3 border rounded-xl outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Название новой категории"
                            value={newCatName}
                            onChange={(e) => setNewCatName(e.target.value)}
                        />
                        <button onClick={addCategory} className="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold flex items-center gap-2 hover:bg-blue-700 transition">
                            <Plus size={18} /> Добавить
                        </button>
                    </div>
                    <div className="bg-white rounded-xl shadow-sm border divide-y">
                        {categories.map(cat => (
                            <div key={cat.id} className="p-4 flex justify-between items-center hover:bg-gray-50 transition">
                                <span className="font-bold text-gray-700">{cat.name}</span>
                                <button onClick={() => deleteCategory(cat.id)} className="text-red-500 hover:bg-red-50 p-2 rounded-lg transition">
                                    <Trash2 size={18} />
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {activeTab === 'admins' && (
                <div className="max-w-md bg-white p-8 rounded-2xl shadow-sm border border-red-100 text-left">
                    <h3 className="text-xl font-bold mb-2 flex items-center gap-2 text-red-700">
                        <ShieldPlus size={22} /> Регистрация администратора
                    </h3>
                    <p className="text-sm text-gray-500 mb-6">Создание нового системного аккаунта с полным доступом.</p>
                    <form onSubmit={createAdmin} className="space-y-4">
                        <div>
                            <label className="block text-xs font-bold uppercase text-gray-400 mb-1">Email</label>
                            <input type="email" placeholder="admin@example.com" required className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-red-500 outline-none" value={adminEmail} onChange={e => setAdminEmail(e.target.value)} />
                        </div>
                        <div>
                            <label className="block text-xs font-bold uppercase text-gray-400 mb-1">Пароль</label>
                            <input type="password" placeholder="••••••••" required className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-red-500 outline-none" value={adminPass} onChange={e => setAdminPass(e.target.value)} />
                        </div>
                        <button type="submit" className="w-full bg-red-600 text-white py-3 rounded-xl font-bold hover:bg-red-700 transition shadow-md mt-2">Создать аккаунт</button>
                    </form>
                </div>
            )}
        </div>
    );
};