import { configureStore, Store } from "@reduxjs/toolkit";
import baseApi from "../api/index";
import CounterReducer from "../features/counter/counter";

const store: Store = configureStore({
  reducer: {
    counter: CounterReducer,
    [baseApi.reducerPath]: baseApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(baseApi.middleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;
