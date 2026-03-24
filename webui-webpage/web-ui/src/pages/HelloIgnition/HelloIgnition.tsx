import React from "react";
import {
  PageHeader,
} from "@inductiveautomation/ignition-web-ui";
import GatewayInfoComponent from "./components/GatewayInfo";
import DeviceListComponent from "./components/DeviceList";
import "./_styles.scss";

const HelloIgnitionPage = () => {

  return (
    <div className="main-content">
      <PageHeader pageTitle="Hello Ignition"/>
      <div className="gateway-info-section-header">
        <h3>Gateway Information</h3>
      </div>
      <GatewayInfoComponent/>
      <div className="device-section-header">
        <h3>Device Connection List</h3>
      </div>
      <DeviceListComponent />
    </div>
  );
};

export default HelloIgnitionPage;
