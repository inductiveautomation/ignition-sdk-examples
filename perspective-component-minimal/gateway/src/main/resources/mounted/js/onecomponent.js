/**
 * This javascript file was hand-written in pure javascript to demonstrate the minimal implementation required to
 * register a single new component for use in perspective.
 */

const  { Component, ComponentRegistry } = window.PerspectiveClient;

/**
 * Each component requires the definition of a 'Meta Object' which contains information about the component.  This
 * meta object is what is registered to the client-side (web browser environment) component registry.  A similar
 * registration is required on the Gateway (server) side.
 *
 * The structure of the Meta Object is defined by the Typescript interface that is published as part of the
 * `@inductiveautomation/perspective-client` npm package.  Implemented below is the minimal required elements for
 * a component.
 */
class ImageMeta {
    // the string 'type id' for the component.  Best practice is to use reverse domain prefixes to avoid collisions.
    getComponentType() {
        return "com.gh.ia.simple.img";
    }
    // the class or React Type that this component provides
    getViewComponent() {
        return Image;
    }
    // the size of the initial component.
    getDefaultSize() {
        return ({
            width: 360,
            height: 360
        });
    }
    // Invoked when an update to the PropertyTree has occurred,
    // effectively mapping the state of the tree to component props.
    getPropsReducer(tree) {
        return {
            url: tree.readString("url", "")
        };
    }
}

/**
 * The Component implementation.
 *
 * Components in Perspective are React components that extend from perspective-client's `Component` class.
 *
 */
class Image extends Component {
    render() {
        // The props we're interested in.
        const { props: { url }, emit } = this.props;
        // Read the 'url' property provided by the perspective gateway via the component 'props'.
        // Note that the topmost piece of dom requires the application of an element reference, events, style and
        // className as shown below otherwise the layout won't work, or any events configured will fail. See render
        // of MessengerComponent in Messenger.tsx for more details.
        return (React.createElement("img", Object.assign({}, emit(), { src: url, alt: `image-src-${url}` })));
    }
}

ComponentRegistry.register(new ImageMeta())
