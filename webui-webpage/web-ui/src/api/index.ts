import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const baseQuery = fetchBaseQuery({
  baseUrl: "",
  prepareHeaders: (headers) => {
    headers.set("Accept", "application/json");
    return headers;
  },
});

const baseApi = {
  ...createApi({
    baseQuery,
    endpoints: () => ({}),
    keepUnusedDataFor: 0,
  }),
};

export default baseApi;
