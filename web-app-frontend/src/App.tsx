import { CssBaseline } from '@mui/material';
import { Navbar } from 'common/Navbar/Navbar';
import { PageGuard } from 'common/PageGuard/PageGuard';
import { Toaster } from 'common/Styled/Toaster';
import { ActivityFilesView } from 'pages/ActivityFiles/ActivityFilesView';
import { ActivityInstructionsView } from 'pages/ActivityInstructions/ActivityInstructionView';
import { CourseView } from 'pages/Course/CourseView';
import { JoinCourseView } from 'pages/Course/JoinCourseView';
import { CoursesListView } from 'pages/CoursesList/CoursesListView';
import { CreateCourseView } from 'pages/CreateCourse/CreateCourseView';
import { CreateModuleView } from 'pages/CreateModule/CreateModuleView';
import { FilesListView } from 'pages/FilesList/FilesListView';
import { Home } from 'pages/Home/Home';
import { Login } from 'pages/Login/Login';
import { MyModuleView } from 'pages/Module/MyModuleView';
import { MyModulesListView } from 'pages/ModulesList/MyModulesListView';
import { Route, Routes } from 'react-router-dom';
import { PopupError } from 'services/features/error/PopupError';
import { ModalContainer } from 'services/features/modal/ModalContainer';
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
                    path={PageRoutes.JOIN_COURSE}
                    element={
                        <PageGuard
                            authRequirement={ANY_LOGGED_IN}
                            navigationOptionsProvider={(guardedLocation) => ({
                                state: {
                                    redirectTo: guardedLocation.pathname,
                                },
                            })}
                        >
                            <JoinCourseView />
                        </PageGuard>
                    }
                />
                <Route
                    path={PageRoutes.CREATE_COURSE}
                    element={guarded(<CreateCourseView />, withRole(TEACHER))}
                />
                <Route
                    path={PageRoutes.MY_MODULES}
                    element={guarded(<MyModulesListView />, withRole(TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.MY_MODULE}
                    element={guarded(<MyModuleView />, withRole(TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.CREATE_MODULE}
                    element={guarded(<CreateModuleView />, withRole(TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.EDIT_MODULE}
                    element={guarded(<CreateModuleView editMode />, withRole(TECHNICAL_TEACHER))}
                />
                <Route
                    path={PageRoutes.ACTIVITY_INSTRUCTIONS}
                    element={guarded(<ActivityInstructionsView />, ANY_LOGGED_IN)}
                />
                <Route
                    path={PageRoutes.ACTIVITY_FILES}
                    element={guarded(<ActivityFilesView />, ANY_LOGGED_IN)}
                />
                <Route path={PageRoutes.FILES} element={guarded(<FilesListView />, withRole(STUDENT))} />
                <Route path="*" element={guarded(<Home />, ANY_LOGGED_IN)} />
            </Routes>

            <Toaster />
            <PopupError />
            <ModalContainer />
        </>
    );
}

export default App;
