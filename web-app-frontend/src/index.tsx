import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import pl from 'assets/locale/pl.json';
import dayjs from 'dayjs';
import 'dayjs/locale/pl';
import isToday from 'dayjs/plugin/isToday';
import i18n from 'i18next';
import React from 'react';
import ReactDOM from 'react-dom/client';
import { initReactI18next } from 'react-i18next';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from 'services/store';
import App from './App';
import './index.css';

i18n.use(initReactI18next) // passes i18n down to react-i18next
    .init({
        resources: {
            pl: {
                translation: pl,
            },
        },
        lng: 'pl', // if you're using a language detector, do not define the lng option
        fallbackLng: 'pl',
        interpolation: {
            escapeValue: false, // react already safes from xss
        },
    });

dayjs.extend(isToday);

// TODO navigator.language (?)
dayjs.locale('pl');

// TODO different config in prod
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            refetchOnWindowFocus: false,
            retry: 0,
        },
    },
});

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <React.StrictMode>
        <Provider store={store}>
            <QueryClientProvider client={queryClient}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <BrowserRouter>
                        <App />
                    </BrowserRouter>
                </LocalizationProvider>
            </QueryClientProvider>
        </Provider>
    </React.StrictMode>
);
