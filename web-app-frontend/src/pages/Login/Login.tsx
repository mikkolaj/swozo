import { login } from 'Services/features/auth/authSlice';
import { useAppDispatch } from 'Services/store';


const Login = () => {
    const dispatch = useAppDispatch();
    
    return (
        <>
            <button onClick={() => dispatch(login({email: 'admin', password: 'admin'}))}> Test admin login </button>
            <button onClick={() => dispatch(login({email: 'teacher', password: 'teacher'}))}> Test teacher login</button>
        </>
    )
}


export default Login;