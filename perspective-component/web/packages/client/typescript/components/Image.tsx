/**
 * Example of a component which displays an image, given a URL.
 */

import * as React from 'react';
import { Component, ComponentMeta, ComponentProps, SizeObject } from '@inductiveautomation/perspective-client';


// the 'key' or 'id' for this component type.  Component must be registered with this EXACT key in the Java side as well
// as on the client side.  In the client, this is done in the index file where we import and register through the
// ComponentRegistry provided by the perspective-client API.
export const COMPONENT_TYPE = "rad.display.image";


// this is the shape of the properties we get from the perspective 'props' property tree.
export interface ImageProps {
    url: string;   // the url of the image this component should display
}


export class Image extends Component<ComponentProps, any> {
    render() {
        // the props we're interested in

        const { props } = this.props;
        // read the 'url' property provided by the perspective gateway via the component 'props'.
        const propUrl: string = props.read('url');

        // note that the topmost piece of dom requires the application of events, style and className as shown below
        // otherwise the layout won't work, or any events configured will fail.
        return (
            <img
                {...this.props.emit()}
                src={propUrl}
            />
        );
    }
}


// this is the actual thing that gets registered with the component registry
export class ImageMeta implements ComponentMeta {

    getComponentType(): string {
        return COMPONENT_TYPE;
    }

    // the class or React Type that this component provides
    getViewClass(): React.ReactType {
        return Image;
    }

    getDefaultSize(): SizeObject {
        return ({
            width: 360,
            height: 360
        });
    }
}
