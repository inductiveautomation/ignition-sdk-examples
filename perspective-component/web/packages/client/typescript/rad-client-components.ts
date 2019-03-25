import {ComponentMeta, ComponentRegistry} from '@inductiveautomation/perspective-client';
import { ImageMeta } from './Image';

export {Image, ImageMeta} from './Image';



// as new components are implemented, import them, and add their meta to this array
const components: Array<ComponentMeta> = [
    new ImageMeta()
];

// iterate through our components, registering each one with the registry.  Don't forget to register on the Java side too!
components.forEach((c: ComponentMeta) => ComponentRegistry.register(c) );
