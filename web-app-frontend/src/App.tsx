import { getApis } from 'api/initialize-apis';
import Login from 'pages/Login/Login';
import { useEffect, useState } from 'react';
import { useAppSelector } from 'Services/store';

function App() {
    const { authData } = useAppSelector(({ auth }) => auth);
    const [connState, setConnState] = useState('loading');
    const [shouldTest, setShouldTest] = useState(false);
    const [roleToTest, setRoleToTest] = useState('');

    useEffect(() => {
        const testConn = async () => {
            try {
                let data;

                if (roleToTest === '') data = await getApis().exampleApi.getExample();
                else if (roleToTest === 'ADMIN') data = await getApis().exampleApi.getExampleJson();

                console.log(data);
                setConnState(JSON.stringify(data));
            } catch (err) {
                setConnState(`error occurred: ${JSON.stringify(err)}`);
            }
        };

        if (shouldTest) {
            testConn();
            setShouldTest(false);
        }
    }, [shouldTest, roleToTest]);

    return (
        <>
            <Login />
            <button
                onClick={() => {
                    setRoleToTest('');
                    setShouldTest(true);
                }}
            >
                test jwt no roles
            </button>
            <button
                onClick={() => {
                    setRoleToTest('ADMIN');
                    setShouldTest(true);
                }}
            >
                test admin role
            </button>
            <div>{JSON.stringify(connState)}</div>
            <div>auth data from backend: {JSON.stringify(authData)}</div>
        </>
    );
}

export default App;
