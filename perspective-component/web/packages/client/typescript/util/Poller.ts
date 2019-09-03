import Axios, { AxiosResponse } from 'axios';


/**
 * Poller: calls the callback with every response. If the callback returns
 * (a promise that resolves to) false the poller is stopped. In all other cases
 * the poller continues.
 */
export class Poller<T = any> {
    private timeoutId: number | undefined;

    private callback: (response: AxiosResponse<T>) => any;

    private _interval: number;

    readonly resourceUrl: string;

    constructor(resourceUrl: string, interval: number) {
        this.resourceUrl = resourceUrl;
        this._interval = interval;
        this.start = this.start.bind(this);
        this.begin = this.begin.bind(this);
        this.updateInterval = this.updateInterval.bind(this);
        this.stop = this.stop.bind(this);
    }


    start(callback: (resource: any) => any) {
        this.callback = callback;
        this.begin();
    }


    private begin() {
        if (!this.timeoutId) {
            (async () => await this.poll())();
        }

        this.schedulePoll();
    }

    private schedulePoll() {
        this.timeoutId = window.setTimeout(
            async () => {
                if (await this.poll() !== false) {
                    this.schedulePoll();
                }
            },
            this._interval
        );
    }

    updateInterval(interval: number = 1000) {
        if (this._interval !== interval) {
            stop();
            this._interval = interval;
            this.begin();
        }
    }

    private async poll<T = any>(): Promise<T> {
        const response = await Axios.get(this.resourceUrl);
        return this.callback(response);
    }

    stop() {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
            this.timeoutId = undefined;
        }
    }

    get started() {
        return this.timeoutId !== undefined;
    }

    get interval() {
        return this._interval;
    }
}
