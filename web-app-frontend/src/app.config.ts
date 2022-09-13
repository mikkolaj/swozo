import pl from 'assets/locale/pl.json';
import dayjs from 'dayjs';
import 'dayjs/locale/pl';
import isToday from 'dayjs/plugin/isToday';
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { QueryClient } from 'react-query';

export type AppConfig = {
    queryClient: QueryClient;
};

export const IS_PROD = process.env.REACT_APP_ENV === 'prod';
export const DEFAULT_LANG = 'pl';

function configureQueryClient() {
    return IS_PROD
        ? new QueryClient()
        : new QueryClient({
              defaultOptions: {
                  queries: {
                      refetchOnWindowFocus: false,
                      retry: 0,
                  },
              },
          });
}

function configureLogger() {
    const noop = () => undefined;

    if (IS_PROD) {
        console.log = console.debug = noop;
    }
}

function configureI18n() {
    i18n.use(initReactI18next) // passes i18n down to react-i18next
        .init({
            resources: {
                pl: {
                    translation: pl,
                },
            },
            lng: DEFAULT_LANG, // if you're using a language detector, do not define the lng option
            fallbackLng: DEFAULT_LANG,
            interpolation: {
                escapeValue: false, // react already safes from xss
            },
        });
}

function configureDateTimeSystem() {
    dayjs.extend(isToday);

    // TODO navigator.language (?)
    dayjs.locale(DEFAULT_LANG);
}

export function configureEnvironment(): AppConfig {
    configureLogger();
    configureI18n();
    configureDateTimeSystem();

    return {
        queryClient: configureQueryClient(),
    };
}
