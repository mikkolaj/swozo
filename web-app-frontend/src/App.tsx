import { CssBaseline } from '@mui/material';
import { Navbar } from 'common/Navbar/Navbar';
import { ActivityInstructionsView } from 'pages/ActivityInstructions/ActivityInstructionViews';
import { CourseView } from 'pages/Course/CourseView';
import { CoursesListView } from 'pages/CoursesList/CoursesListView';
import { CreateCourseView } from 'pages/CreateCourse/CreateCourseView';
import { CreateModuleView } from 'pages/CreateModule/CreateModuleView';
import { FilesListView } from 'pages/FilesList/FilesListView';
import { Home } from 'pages/Home/Home';
import Login from 'pages/Login/Login';
import { ModulesListView } from 'pages/ModulesList/ModulesListView';
import { Route, Routes } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useAppSelector } from 'services/store';

import {
    ANY_LOGGED_IN,
    guarded,
    NOT_LOGGED_IN,
    STUDENT,
    TEACHER,
    TECHNICAL_TEACHER,
    withRole,
} from 'utils/roles';
import { PageRoutes } from 'utils/routes';

function App() {
    const isLoggedIn = useAppSelector((state) => state.auth.isLoggedIn);

    return (
        <>
            <CssBaseline />
            {isLoggedIn && <Navbar />}
            <Routes>
                <Route path={PageRoutes.HOME} element={guarded(<Home />, ANY_LOGGED_IN)} />
                <Route path={PageRoutes.LOGIN} element={guarded(<Login />, NOT_LOGGED_IN)} />
                <Route path={PageRoutes.MY_COURSES} element={guarded(<CoursesListView />, ANY_LOGGED_IN)} />
                <Route path={PageRoutes.COURSE} element={guarded(<CourseView />, ANY_LOGGED_IN)} />
                <Route
                    path={PageRoutes.CREATE_COURSE}
                    element={guarded(<CreateCourseView />, withRole(TEACHER))}
                />
                <Route
                    path={PageRoutes.MY_MODULES}
                    element={guarded(<ModulesListView />, withRole(TEACHER, TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.CREATE_MODULE}
                    element={guarded(<CreateModuleView />, withRole(TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.ACTIVITY_INSTRUCTIONS}
                    element={guarded(<ActivityInstructionsView />, ANY_LOGGED_IN)}
                />
                <Route path={PageRoutes.FILES} element={guarded(<FilesListView />, withRole(STUDENT))} />
                <Route path="*" element={guarded(<Home />, ANY_LOGGED_IN)} />
            </Routes>

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
        </>
    );
}

export default App;
