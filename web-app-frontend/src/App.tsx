import { CssBaseline } from '@mui/material';
import { Navbar } from 'common/Navbar/Navbar';
import { PageGuard } from 'common/PageGuard/PageGuard';
import { CourseView } from 'pages/Course/CourseView';
import { CoursesListView } from 'pages/CoursesList/CoursesListView';
import { CreateCourseView } from 'pages/CreateCourse/CreateCourseView';
import { Home } from 'pages/Home/Home';
import Login from 'pages/Login/Login';
import { ModulesListView } from 'pages/ModulesList/ModulesListView';
import { Route, Routes } from 'react-router-dom';
import { useAppSelector } from 'services/store';
import {
    ANY_LOGGED_IN,
    AuthRequirement,
    NOT_LOGGED_IN,
    TEACHER,
    TECHNICAL_TEACHER,
    withRole,
} from 'utils/roles';
import { PageRoutes } from 'utils/routes';

export const guarded = (element: JSX.Element, authRequirement: AuthRequirement): JSX.Element => {
    return <PageGuard authRequirement={authRequirement}>{element}</PageGuard>;
};

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
                <Route path="*" element={guarded(<Home />, ANY_LOGGED_IN)} />
            </Routes>
        </>
    );
}

export default App;
