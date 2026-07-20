// Named exports here become the "component ids" the gateway hook mounts via
// nav .mount(url, "WebUiExamples", jsModule). The bundle's UMD global is set by the
// webpack `entry` name (webuiExamples), and the file is served at
// /res/web-ui-test/webuiExamples.js.
export { default as WebUiExamples } from "./pages/WebUiExamples";
