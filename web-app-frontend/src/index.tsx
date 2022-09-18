import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AppConfig, configureEnvironment } from 'app.config';
import React from 'react';
import ReactDOM from 'react-dom/client';
import { QueryClientProvider } from 'react-query';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import { store } from 'services/store';
import App from './App';
import './index.css';

const appConfig: AppConfig = configureEnvironment();

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <React.StrictMode>
        <Provider store={store}>
            <QueryClientProvider client={appConfig.queryClient}>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <BrowserRouter>
                        <App />
                    </BrowserRouter>
                </LocalizationProvider>
            </QueryClientProvider>
        </Provider>
    </React.StrictMode>
);
