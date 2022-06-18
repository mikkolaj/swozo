import pl from 'assets/locale/pl.json';
import dayjs from 'dayjs';
import 'dayjs/locale/pl';
import isToday from 'dayjs/plugin/isToday';
import i18n from 'i18next';
import React from 'react';
import ReactDOM from 'react-dom/client';
import { initReactI18next } from 'react-i18next';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from 'services/store';
import App from './App';
import './index.css';
import reportWebVitals from './reportWebVitals';

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

dayjs.locale('pl');
dayjs.extend(isToday);

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <React.StrictMode>
        <Provider store={store}>
            <BrowserRouter>
                <App />
            </BrowserRouter>
        </Provider>
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
