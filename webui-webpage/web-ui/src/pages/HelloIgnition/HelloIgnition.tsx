import React, { useEffect } from "react";
import {
  Button,
  ButtonColorClasses,
  // @ts-ignore
} from "@inductiveautomation/ignition-web-ui";
import { Add, AddSmall, Reset } from "@inductiveautomation/ignition-icons";
import { increment, addTwo, reset } from "../../features/counter/counter";
import { useSelector, useDispatch } from "react-redux";
import { useGetInfoQuery } from "./HelloIgnition.service";
import "./_styles.scss";

const HelloIgnitionPage = () => {
  const dispatch = useDispatch();
  // @ts-ignore
  const count = useSelector((state) => state.counter);

  const { data, isLoading, error } = useGetInfoQuery("info", {
    selectFromResult: ({ data, isLoading, error }) => ({
      data,
      isLoading,
      error,
    }),
  });

  useEffect(() => {
    if (error) {
      console.error(error);
    }
  }, [error]);

  return (
    <div className="main-content">
      <p>Hello Ignition</p>

      <h3>Counter: {count}</h3>

      <div className="button-container">
        <Button
          onClick={() => dispatch(increment())}
          colorClass={ButtonColorClasses.SECONDARY}
          endIcon={<AddSmall height={16} width={16} data-icon="add-small" />}
        >
          Increment
        </Button>

        <Button
          onClick={() => dispatch(addTwo())}
          colorClass={ButtonColorClasses.SECONDARY}
          endIcon={<Add height={16} width={16} data-icon="add" />}
        >
          Add Two
        </Button>

        <Button
          onClick={() => dispatch(reset())}
          colorClass={ButtonColorClasses.SECONDARY}
          endIcon={<Reset height={16} width={16} data-icon="reset" />}
        >
          Reset
        </Button>
      </div>

      <div className="info">
        {data && !isLoading ? (
          <div>
            <p>Name: {data.name}</p>
            <p>Username: {data.username}</p>
            <p>Email: {data.email}</p>
            <p>Phone: {data.phone}</p>
          </div>
        ) : (
          <p>...Loading</p>
        )}
      </div>
    </div>
  );
};

export default HelloIgnitionPage;
