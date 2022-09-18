import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export const Toaster = () => (
    <ToastContainer
        position="bottom-right"
        autoClose={2000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
    />
);
