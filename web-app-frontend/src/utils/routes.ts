export class PageRoutes {
    static readonly HOME = '/';
    static readonly LOGIN = '/login';
    static readonly MY_COURSES = '/my-courses';
    static readonly COURSE = `${this.MY_COURSES}/:courseId`;
    static readonly CREATE_COURSE = `${this.MY_COURSES}/creator`;
    static readonly PUBLIC_COURSES = '/courses';
    static readonly JOIN_COURSE = `${this.PUBLIC_COURSES}/join/:joinUUID`;
    static readonly MY_MODULES = '/my-modules';
    static readonly CREATE_MODULE = `${this.MY_MODULES}/creator`;
    static readonly MY_MODULE = `${this.MY_MODULES}/:moduleId`;
    static readonly EDIT_MODULE = `${this.MY_MODULE}/editor`;
    static readonly PUBLIC_MODULES = '/modules';
    static readonly PUBLIC_MODULE = `${this.PUBLIC_MODULES}/:moduleId`;
    static readonly ACTIVITIES = `${this.COURSE}/activities`;
    static readonly ACTIVITY = `${this.ACTIVITIES}/:activityId`;
    static readonly ACTIVITY_INSTRUCTIONS = `${this.ACTIVITY}/instructions`;
    static readonly ACTIVITY_FILES = `${this.ACTIVITY}/files`;
    static readonly FILES = '/files';
    static readonly ADMIN = '/admin';
    static readonly ADMIN_USER_DETAILS = `${this.ADMIN}/users/:userId`;
    static readonly VIRTUAL_MACHINES = `${this.ADMIN}/virtual-machines`;
    static readonly UPCOMING_ACTIVITIES = `${this.ADMIN}/upcoming-activities`;
    static readonly SERVICES = '/services';
    static readonly HELP = '/help';

    static Course(courseId: string | number): string {
        return RouteBuilder.of(this.COURSE).withReplaced('courseId', courseId).build();
    }

    static MyModule(moduleId: string | number): string {
        return RouteBuilder.of(this.MY_MODULE).withReplaced('moduleId', moduleId).build();
    }

    static EditModule(moduleId: string | number): string {
        return RouteBuilder.of(this.EDIT_MODULE).withPrefix(this.MyModule(moduleId)).build();
    }

    static PublicModule(moduleId: string | number): string {
        return RouteBuilder.of(this.PUBLIC_MODULE).withReplaced('moduleId', moduleId).build();
    }

    static Activity(courseId: string | number, activityId: string | number): string {
        return RouteBuilder.of(this.ACTIVITY)
            .withPrefix(this.Course(courseId))
            .withReplaced('activityId', activityId)
            .build();
    }

    static ActivityInstructions(courseId: string | number, activityId: string | number): string {
        return RouteBuilder.of(this.ACTIVITY_INSTRUCTIONS)
            .withPrefix(this.Activity(courseId, activityId))
            .build();
    }

    static ActivityFiles(courseId: string | number, activityId: string | number): string {
        return RouteBuilder.of(this.ACTIVITY_FILES).withPrefix(this.Activity(courseId, activityId)).build();
    }

    static JoinCourse(joinUUID: string): string {
        return RouteBuilder.of(this.JOIN_COURSE).withReplaced('joinUUID', joinUUID).build();
    }

    static AdminUserDetails(userId: string | number): string {
        return RouteBuilder.of(this.ADMIN_USER_DETAILS).withReplaced('userId', userId).build();
    }

    static withOrigin(route: string): string {
        return `${window.location.origin}${route}`;
    }
}

type RoutePlaceholder = {
    k: string;
    v: string | number;
};

class RouteBuilder {
    private prefix: string;
    private _route: string;
    private placeholders: RoutePlaceholder[];

    private constructor(route: string) {
        this.prefix = '';
        this.placeholders = [];
        this._route = route;
    }

    static of(targetRoute: string): RouteBuilder {
        return new RouteBuilder(targetRoute);
    }

    withPrefix(prefix: string): RouteBuilder {
        this.prefix = prefix;
        return this;
    }

    withReplaced(placeholder: string, value: string | number): RouteBuilder {
        this.placeholders.push({ k: placeholder, v: value });
        return this;
    }

    build(): string {
        return this.prefix ? this.replaceUsingPrefix() : this.replace();
    }

    private replaceUsingPrefix(): string {
        const prefixSubRoutes = this.prefix.split('/').length;
        return this.prefix + '/' + this.replace().split('/').slice(prefixSubRoutes).join('/');
    }

    private replace(): string {
        let result = this._route;

        this.placeholders.forEach(({ k: key, v: value }) => {
            result = result.replace(`:${key}`, value.toString());
        });

        return result;
    }
}
