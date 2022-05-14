import React, { useEffect, useState } from 'react';

function App() {
    const [connState, setConnState] = useState('loading');

    useEffect(() => {
        const testConn = async () => {
            try {
                const data = await fetch('http://localhost:5000/example').then((resp) => resp.text());
                const jsonData = await fetch('http://localhost:5000/example/json').then((resp) => resp.json());
                console.log(data);
                console.log(jsonData);
                setConnState(data + JSON.stringify(jsonData));
            } catch (err) {
                setConnState(`no connection: ${err}`);
            }
        };

        testConn();
    }, []);

    return (
        <div>
            Hello {connState}
        </div>
    );
}

export default App;
