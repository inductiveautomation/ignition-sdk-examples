/**
 * Example of a component which displays an image, given a URL.
 */
import { AxiosResponse } from 'axios';
import * as React from 'react';
import {
    Component,
    ComponentMeta,
    ComponentProps, PComponent,
    PropertyTree,
    SizeObject
} from '@inductiveautomation/perspective-client';
import { Poller } from '../util/Poller';
import { bind } from 'bind-decorator';

// The 'key' or 'id' for this component type.  Component must be registered with this EXACT key in the Java side as well
// as on the client side.  In the client, this is done in the index file where we import and register through the
// ComponentRegistry provided by the perspective-client API.
export const COMPONENT_TYPE = "rad.display.tagcounter";

interface TagCountPayload {
    tagCount: number;
}

interface TagCountProps {
    interval: number;
}

const HOST = `${location.protocol}//${location.host}`;
const COUNT_FETCH_URL = `${HOST}/main/data/radcomponents/component/tagcount`;

interface TagCounterState {
    tagCount: number;
    animating: boolean;
}

/**
 * This example uses a 'data route' to collect tag counts according to a client-side polling request.
 */
export class TagCounter extends Component<ComponentProps<TagCountProps>, TagCounterState> {
    state: TagCounterState = {
        tagCount: 0,
        animating: false
    };
    fetchPoller?: Poller<TagCountPayload>;

    componentDidMount() {
        this.fetchPoller = new Poller<TagCountPayload>(COUNT_FETCH_URL, this.props.props.interval);
        this.fetchPoller.start(this.updateTagCount);
    }

    componentWillUnmount(): void {
        this.fetchPoller && this.fetchPoller.stop();
        this.fetchPoller = undefined;
    }

    @bind
    updateTagCount(response: AxiosResponse<TagCountPayload>): void {
        if (!this.state.animating) {
            this.setState({ animating: true });
            setTimeout(() => this.setState({ animating: false }), this.props.props.interval);
        }

        if (response && response.status === 200) {
            const json = response.data;

            if (json && json.tagCount !== undefined) {
                this.setState({ tagCount: json.tagCount });
            } else {
                console.warn(`UpdateTagCount() called with unknown argument '${JSON.stringify(json)}'`);
            }
        } else {
            console.warn(`Failed to collect updated tag count. Received response ` +
                `'${response.status} - ${response.statusText}'`);
        }
    }

    render() {
        const { props, emit } = this.props;
        const interval = props.interval;

        if (this.fetchPoller && (this.fetchPoller.interval !== interval)) {
            this.fetchPoller.updateInterval(interval);
        }

        const counterClasses = this.state.animating ? 'tag-counter-count message-animation' : 'tag-counter-count';

        // Note that the topmost piece of dom requires the application of an element reference, events, style and
        // className as shown below otherwise the layout won't work, or any events configured will fail. See render
        // of MessengerComponent in Messenger.tsx for more details.
        return (
            <div {...emit({ classes: ['tag-counter-component'] })}>
                <span className={counterClasses}>{this.state.tagCount}</span>
                <span className={"tag-counter-interval"}>{`Interval ${interval} ms`}</span>
            </div>

        );
    }
}


// This is the actual thing that gets registered with the component registry.
export class TagCounterMeta implements ComponentMeta {
    getComponentType(): string {
        return COMPONENT_TYPE;
    }

    getDefaultSize(): SizeObject {
        return ({
            width: 160,
            height: 64
        });
    }

    getPropsReducer(tree: PropertyTree): Record<string, any> {
        return {
            interval: tree.readNumber("interval", 1000)
        };
    }

    getViewComponent(): PComponent {
        return TagCounter;
    }
}
