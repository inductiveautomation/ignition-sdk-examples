import baseApi from "../../api/index";

export const { useGetInfoQuery } = baseApi.injectEndpoints({
  endpoints: (builder) => ({
    getInfo: builder.query<any, string>({
      query: (queryParams) => `https://jsonplaceholder.typicode.com/users/1`,
    }),
  }),
  overrideExisting: false,
});
