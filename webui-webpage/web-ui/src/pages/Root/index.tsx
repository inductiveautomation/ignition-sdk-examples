import React from "react";
import { Provider } from "react-redux";
import store from "../../store";
import HelloIgnition from "../HelloIgnition";

const RootPage = () => {
  return (
    <Provider store={store}>
      <HelloIgnition />
    </Provider>
  );
};

export default RootPage;
