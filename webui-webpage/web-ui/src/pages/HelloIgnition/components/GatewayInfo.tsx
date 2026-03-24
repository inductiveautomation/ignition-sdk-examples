import React, { useEffect } from "react";
import {
  OverviewCard,
  useToastNotifications,
} from "@inductiveautomation/ignition-web-ui";

import { useGetInfoQuery } from "../HelloIgnition.service";
import "../_styles.scss";

const GatewayInfoComponent = () => {

  const { data={}, isSuccess, isError, error } = useGetInfoQuery("info", {
    selectFromResult: ({ data, isSuccess, isError, error }) => ({
      data,
      isSuccess,
      isError,
      error,
    }),
  });
  const { notifyError } = useToastNotifications();

  useEffect(() => {
    if (isError) {
      console.error(error);
      notifyError("Error retrieving Gateway information.");
    } else {
      if (isSuccess) {
        console.log(JSON.stringify(data, null, 2));
      }
    }
  }, [error]);

  return (
    <div className="gateway-info">
      <div>
        <OverviewCard
          actions={[]}
          metrics={[
            {
              label: "Name",
              value: data.name,
            },
            {
              label: "Ignition Version",
              value: data.ignitionVersion,
            },
            {
              label: "JVM Version",
              value: data.jvmVersion,
            },
          ]}
          title="Gateway Info"
        />
      </div>
    </div>
  );
};

export default GatewayInfoComponent;