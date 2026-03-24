import baseApi from "../../api/index";

export const { useGetInfoQuery, useGetDevicesQuery } = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInfo: builder.query<any, string>({
      query: (queryParams) => `/data/api/v1/gateway-info`,
    }),
    getDevices: builder.query<any, string>({
      query: (queryParams) =>
        `/data/api/v1/resources/list/com.inductiveautomation.opcua/device${queryParams}`,
    }),
  }),
  overrideExisting: false,
});
