import baseApi from "../../api/index";
import deviceList from "../../models/deviceList";

export const { useGetInfoQuery, useGetDevicesQuery } = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInfo: builder.query<any, string>({
      query: (queryParams) => `http://localhost:8088/data/api/v1/gateway-info`,
    }),
    getDevices: builder.query<any, string>({
      query: (queryParams) =>
        `http://localhost:8088/data/api/v1/resources/list/com.inductiveautomation.opcua/device`,
      transformResponse: (response: any) => {
        if (response.status === "error") {
          throw new Error(`Request failed with reason: ${response.message}`);
        }

        console.log("Raw data:", response);
        const transformedData = response.items.reduce(
          (acc: deviceList[], curr) => {
            acc.push(
              new deviceList(
                curr.name,
                curr.enabled,
                curr.healthchecks.status.result.healthy
              )
            );
            console.log("Accumulated data:", acc);
            return acc;
          },
          []
        );
        console.log("Transformed data:", transformedData);
        return transformedData;
      },
    }),
  }),
  overrideExisting: false,
});
