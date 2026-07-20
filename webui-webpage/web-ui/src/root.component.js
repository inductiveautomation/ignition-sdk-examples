import React from "react";
import { createRoot } from "react-dom/client";
import { WebUiExamples } from "./index";

// Local development harness (used only by `yarn run:dev`). In the gateway, the platform mounts the
// exported component itself; this file is not part of the production bundle.
const root = createRoot(document.getElementById("root"));
root.render(<WebUiExamples />);
