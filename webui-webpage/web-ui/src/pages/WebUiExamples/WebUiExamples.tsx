import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Button,
  ButtonColorClasses,
  PageHeader,
  // @ts-ignore - the gateway provides these types/implementations at runtime
} from "@inductiveautomation/ignition-web-ui";
import { Reset } from "@inductiveautomation/ignition-icons";
import "./_styles.scss";

/**
 * The module id, which is also the first segment of every resource type string.
 */
const MODULE_ID = "org.webui.test.WebuiWebpage";

/**
 * The platform mounts CRUD routes for every registered resource type under this base path. The
 * module itself ships no server code for these - registering a `ResourceTypeMeta` with a route
 * delegate (see the gateway sources) is enough for the platform to serve them.
 */
const RESOURCES_API = "/data/api/v1/resources";

const GREETING_TYPE = `${MODULE_ID}/greeting`;
const SETTINGS_TYPE = `${MODULE_ID}/greeting-settings`;
const PROVIDER_TYPE = `${MODULE_ID}/greeting-provider`;

/** One row as returned by the resource list/singleton routes. */
interface ResourceItem<T = any> {
  name: string;
  enabled: boolean;
  description?: string;
  collection?: string;
  config: T;
}

interface GreetingConfig {
  message: string;
  tone: string;
  repeatCount: number;
  shout: boolean;
}

interface GreetingSettings {
  pageTitle: string;
  showTimestamp: boolean;
  refreshIntervalMs: number;
}

/** The extension point resource's config is split into a shared `profile` and typed `settings`. */
interface ProviderConfig {
  profile: { type: string; prefix?: string | null };
  settings?: Record<string, any>;
}

async function getJson<T>(url: string): Promise<T> {
  const res = await fetch(url, { headers: { Accept: "application/json" } });
  if (!res.ok) {
    throw new Error(`${res.status} ${res.statusText} for ${url}`);
  }
  return res.json();
}

const WebUiExamples = () => {
  const [settings, setSettings] =
    useState<ResourceItem<GreetingSettings> | null>(null);
  const [greetings, setGreetings] = useState<ResourceItem<GreetingConfig>[]>(
    []
  );
  const [providers, setProviders] = useState<ResourceItem<ProviderConfig>[]>(
    []
  );
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

  const refresh = useCallback(async () => {
    try {
      const [settingsResp, greetingsResp, providersResp] = await Promise.all([
        getJson<ResourceItem<GreetingSettings>>(
          `${RESOURCES_API}/singleton/${SETTINGS_TYPE}`
        ),
        getJson<{ items: ResourceItem<GreetingConfig>[] }>(
          `${RESOURCES_API}/list/${GREETING_TYPE}`
        ),
        getJson<{ items: ResourceItem<ProviderConfig>[] }>(
          `${RESOURCES_API}/list/${PROVIDER_TYPE}`
        ),
      ]);
      setSettings(settingsResp);
      setGreetings(greetingsResp.items ?? []);
      setProviders(providersResp.items ?? []);
      setError(null);
      setLastUpdated(new Date());
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }, []);

  // Initial load + optional auto-refresh driven by the singleton settings resource.
  const refreshIntervalMs = settings?.config?.refreshIntervalMs ?? 0;
  useEffect(() => {
    refresh();
  }, [refresh]);
  useEffect(() => {
    if (!refreshIntervalMs) {
      return undefined;
    }
    const handle = window.setInterval(refresh, refreshIntervalMs);
    return () => window.clearInterval(handle);
  }, [refresh, refreshIntervalMs]);

  const pageTitle = settings?.config?.pageTitle ?? "WebUI Examples";
  const renderGreeting = useMemo(
    () => (config: GreetingConfig) => {
      const text = config.shout
        ? (config.message || "").toUpperCase()
        : config.message;
      return Array(Math.max(1, config.repeatCount || 1))
        .fill(text)
        .join(" ");
    },
    []
  );

  return (
    <div className="webui-examples">
      <PageHeader pageTitle={pageTitle} />

      <p className="webui-examples__intro">
        This page reads back the module&apos;s three example configuration
        resources through the gateway&apos;s REST API. Create and edit these
        resources with the resource REST routes (documented in the module
        README) or the <code>system.config</code> scripting functions; this page
        then reflects the live configuration.
      </p>

      <div className="webui-examples__toolbar">
        <Button
          onClick={refresh}
          colorClass={ButtonColorClasses.SECONDARY}
          endIcon={<Reset height={16} width={16} data-icon="reset" />}
        >
          Refresh
        </Button>
        {settings?.config?.showTimestamp && lastUpdated && (
          <span className="webui-examples__timestamp">
            Last updated {lastUpdated.toLocaleTimeString()}
          </span>
        )}
      </div>

      {error && (
        <div className="webui-examples__error">
          Failed to load resources: {error}
        </div>
      )}
      {loading && <p>Loading…</p>}

      {/* 1. NAMED resource */}
      <section>
        <h3>Named resource — Greetings</h3>
        {greetings.length === 0 ? (
          <p className="webui-examples__empty">
            No greetings configured. Create one with{" "}
            <code>
              POST {RESOURCES_API}/{GREETING_TYPE}
            </code>
            .
          </p>
        ) : (
          <table className="webui-examples__table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Rendered greeting</th>
                <th>Tone</th>
                <th>Enabled</th>
              </tr>
            </thead>
            <tbody>
              {greetings.map((g) => (
                <tr key={g.name}>
                  <td>{g.name}</td>
                  <td>{renderGreeting(g.config)}</td>
                  <td>{g.config.tone}</td>
                  <td>{String(g.enabled)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {/* 2. SINGLETON resource */}
      <section>
        <h3>Singleton resource — Greeting Settings</h3>
        {settings ? (
          <table className="webui-examples__table">
            <tbody>
              <tr>
                <th>Page Title</th>
                <td>{settings.config.pageTitle}</td>
              </tr>
              <tr>
                <th>Show Timestamp</th>
                <td>{String(settings.config.showTimestamp)}</td>
              </tr>
              <tr>
                <th>Refresh Interval (ms)</th>
                <td>{settings.config.refreshIntervalMs}</td>
              </tr>
            </tbody>
          </table>
        ) : (
          <p className="webui-examples__empty">Settings not available.</p>
        )}
      </section>

      {/* 3. EXTENSION POINT resource */}
      <section>
        <h3>Extension point — Greeting Providers</h3>
        {providers.length === 0 ? (
          <p className="webui-examples__empty">
            No greeting providers configured. Create one with{" "}
            <code>
              POST {RESOURCES_API}/{PROVIDER_TYPE}
            </code>
            , choosing a <code>type</code> of <code>static</code> or{" "}
            <code>timeBased</code>.
          </p>
        ) : (
          <table className="webui-examples__table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Prefix</th>
                <th>Enabled</th>
              </tr>
            </thead>
            <tbody>
              {providers.map((p) => (
                <tr key={p.name}>
                  <td>{p.name}</td>
                  <td>{p.config?.profile?.type}</td>
                  <td>{p.config?.profile?.prefix ?? <em>none</em>}</td>
                  <td>{String(p.enabled)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
};

export default WebUiExamples;
