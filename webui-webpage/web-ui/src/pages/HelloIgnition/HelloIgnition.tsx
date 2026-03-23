import React, { useEffect, useState } from "react";
import { OverviewCard, DataGrid } from "@inductiveautomation/ignition-web-ui";
import {
  useSelector,
  //   useDispatch
} from "react-redux";
import { useGetInfoQuery, useGetDevicesQuery } from "./HelloIgnition.service";
// import deviceList from "../../models/deviceList";
import "./_styles.scss";

const HelloIgnitionPage = () => {
  //   const dispatch = useDispatch();
  // @ts-ignore
  const count = useSelector((state) => state.counter);

  //   const { data, isLoading, isError } = useGetInfoQuery("info", {
  //     selectFromResult: (result) => ({ ...result }),
  //     pollingInterval: 3000,
  //   });

  const infoResult = useGetInfoQuery("info");
  const deviceResult = useGetDevicesQuery("devices");
  console.log(infoResult);
  console.log(deviceResult);

  const [, setQueryParams] = useState<string>("");

  useEffect(() => {
    if (infoResult.isError) {
      console.error(infoResult.error);
    } else {
      if (infoResult.isSuccess) {
        console.log(JSON.stringify(infoResult, null, 2));
      }
    }

    if (deviceResult.isError) {
      console.error(deviceResult.error);
    } else {
      if (deviceResult.isSuccess) {
        console.log(JSON.stringify(deviceResult.data, null, 2));
      }
    }
  }, [deviceResult.error, infoResult.error]);

  return (
    <div className="main-content">
      <p>Hello Ignition</p>

      <div className="info">
        {infoResult.isSuccess ? (
          <div>
            <OverviewCard
              actions={[]}
              metrics={[
                {
                  label: "Name",
                  value: infoResult.data.name,
                },
                {
                  label: "Ignition Version",
                  value: infoResult.data.ignitionVersion,
                },
                {
                  label: "JVM Version",
                  value: infoResult.data.jvmVersion,
                },
              ]}
              title="Gateway Info"
            />
          </div>
        ) : (
          <p>...Loading</p>
        )}
      </div>
      {deviceResult.isSuccess ? (
        <div className="devices">
          <DataGrid
            actionButtons={[]}
            paginationParams={{
              matching: 0,
              total: 0,
              limit: 20,
              offset: 0,
            }}
            setTableQueryParams={setQueryParams}
            columnDefs={[
              {
                fieldName: "deviceName",
                header: "Device Name",
                width: 200,
                sortable: true,
              },
              {
                fieldName: "enabled",
                accessorKey: "deviceList.enabled",
                header: "Enabled",
              },
              {
                fieldName: "valid",
                accessorKey: "deviceList.valid",
                header: "Connected?",
              },
            ]}
            data={deviceResult.data}
            id="storybook-data-grid"
            isLoading={deviceResult.isLoading}
            itemName="User"
            multiSelectActionButtons={[]}
          />
        </div>
      ) : (
        <p>Query Error</p>
      )}
    </div>
  );
};

export default HelloIgnitionPage;
