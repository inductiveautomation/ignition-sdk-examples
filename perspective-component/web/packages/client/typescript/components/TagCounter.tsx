/**
 * Example of a component which displays an image, given a URL.
 */

import { AxiosResponse } from 'axios';
import { observable } from 'mobx';
import { observer } from 'mobx-react';
import * as React from 'react';
import { Component, ComponentMeta, ComponentProps, SizeObject } from '@inductiveautomation/perspective-client';
import { Poller } from '../util/Poller';
import { bind } from 'bind-decorator';


// the 'key' or 'id' for this component type.  Component must be registered with this EXACT key in the Java side as well
// as on the client side.  In the client, this is done in the index file where we import and register through the
// ComponentRegistry provided by the perspective-client API.
export const COMPONENT_TYPE = "rad.display.tagcounter";

interface TagCountPayload {
    tagCount: number;
}


const HOST = `${location.protocol}//${location.host}`;
const COUNT_FETCH_URL = `${HOST}/main/data/radcomponents/component/tagcount`;
/**
 * This example uses a 'data route' to collect tag counts according to a client-side polling request.
 */
@observer
export class TagCounter extends Component<ComponentProps, {}> {

    fetchPoller?: Poller<TagCountPayload>;

    @observable tagCount: number = 0;

    @observable animating: boolean = false;

    componentDidMount() {
        this.fetchPoller = new Poller<TagCountPayload>(COUNT_FETCH_URL,  this.props.props.read("interval", 1000));
        this.fetchPoller.start(this.updateTagCount);
    }

    componentWillUnmount(): void {
        this.fetchPoller && this.fetchPoller.stop();
        this.fetchPoller = undefined;
    }

    @bind
    updateTagCount(response: AxiosResponse<TagCountPayload>): void {
        if (!this.animating) {
            this.animating = true;
            setTimeout(() => { this.animating = false; },
                       this.props.props.read("interval", 1000)
            );
        }

        if (response && response.status === 200) {
            const json = response.data;

            if (json && json.tagCount !== undefined) {
                this.tagCount = json.tagCount;
            } else {
                console.warn(`UpdateTagCount() called with unknown argument '${JSON.stringify(json)}'`);
            }
        } else {
            console.warn(`Failed to collect updated tag count. Received response ` +
                         `'${response.status} - ${response.statusText}'`);
        }
    }

    render() {
        // the props we're interested in

        const { props } = this.props;
        const interval = props.read("interval", 1000);

        if (this.fetchPoller && (this.fetchPoller.interval !== interval)) {
            this.fetchPoller.updateInterval(interval);
        }

        const counterClasses = this.animating ? 'tag-counter-count message-animation' : 'tag-counter-count';

        // read the 'url' property provided by the perspective gateway via the component 'props'.

        // note that the topmost piece of dom requires the application of events, style and className as shown below
        // otherwise the layout won't work, or any events configured will fail.
        return (
            <div {...this.props.emit({classes: ['tag-counter-component']})}>
                <span className={counterClasses}>{this.tagCount}</span>
                <span className={"tag-counter-interval"}>{`Interval ${interval} ms`}</span>
            </div>

        );
    }
}


// this is the actual thing that gets registered with the component registry
export class TagCounterMeta implements ComponentMeta {
    getComponentType(): string {
        return COMPONENT_TYPE;
    }

    // the class or React Type that this component provides
    getViewClass(): React.ReactType {
        return TagCounter;
    }

    getDefaultSize(): SizeObject {
        return ({
            width: 160,
            height: 64
        });
    }
}
