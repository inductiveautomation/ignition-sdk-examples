import React, { useEffect, useState, useMemo } from "react";
import {
  DataGrid,
  useToastNotifications,
} from "@inductiveautomation/ignition-web-ui";

import { useGetDevicesQuery } from "../HelloIgnition.service";
import "../_styles.scss";

const DeviceListComponent = () => {
  // @ts-ignore
  const [queryParams, setQueryParams] = useState<string>("");

  const defaultData = {
    items: [],
    metadata: {
      total: 0,
      matching: 0,
      limit: 20,
      offset: 0,
    },
  };

  const {
    data = defaultData,
    isSuccess,
    isLoading,
    isError,
    error,
  } = useGetDevicesQuery(queryParams, {
    selectFromResult: ({ data, isSuccess, isLoading, isError, error }) => ({
      data,
      isSuccess,
      isLoading,
      isError,
      error,
    }),
  });

  const { notifyError } = useToastNotifications();

  useEffect(() => {
    if (isError) {
      console.error(error);
      notifyError("Error querying devices.");
    } else {
      if (isSuccess) {
        console.log(JSON.stringify(data, null, 2));
      }
    }
  }, [error]);

  const colDefs = useMemo(() => {
    return [
      {
        fieldName: "name",
        header: "Device Name",
        width: 200,
        sortable: true,
      },
      {
        fieldName: "enabled",
        setCellValue: ({ enabled }) => {
          return `${enabled}`;
        },
        header: "Enabled",
      },
      {
        fieldName: "healthchecks.status.result.message",
        header: "Status",
        width: 200,
        sortable: true,
      },
    ];
  }, []);

  return (
    <div className="devices">
      <DataGrid
        actionButtons={[]}
        paginationParams={data.metadata}
        setTableQueryParams={setQueryParams}
        columnDefs={colDefs}
        data={data.items}
        id="device-data-grid"
        isLoading={isLoading}
        itemName="device"
        multiSelectActionButtons={[]}
      />
    </div>
  );
};

export default DeviceListComponent;