export class PageRoutes {
    static readonly HOME = '/';
    static readonly LOGIN = '/login';
    static readonly MY_COURSES = '/my-courses';
    static readonly COURSE = `${this.MY_COURSES}/:courseId`;
    static readonly CREATE_COURSE = `${this.MY_COURSES}/creator`;
    static readonly MY_MODULES = '/my-modules';
    static readonly CREATE_MODULE = `${this.MY_MODULES}/creator`;
    static readonly ACTIVITIES = `${this.COURSE}/activities`;
    static readonly ACTIVITY = `${this.ACTIVITIES}/:activityId`;
    static readonly ACTIVITY_INSTRUCTIONS = `${this.ACTIVITY}/instructions`;
    static readonly FILES = '/files';

    static Course(courseId: string | number): string {
        return RouteBuilder.of(this.COURSE).withReplaced('courseId', courseId).build();
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
