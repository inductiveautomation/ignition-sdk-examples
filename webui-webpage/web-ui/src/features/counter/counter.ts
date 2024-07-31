import { createSlice, PayloadAction } from "@reduxjs/toolkit";

const counterSlice = createSlice({
  name: "counter",
  initialState: 0,
  reducers: {
    increment(state, action: PayloadAction<void>) {
      return state + 1;
    },
    addTwo(state, action: PayloadAction<void>) {
      return state + 2;
    },
    reset(state, action: PayloadAction<void>) {
      return (state = 0);
    },
  },
});

const { actions, reducer } = counterSlice;

export const { increment, addTwo, reset } = actions;

export default reducer;
