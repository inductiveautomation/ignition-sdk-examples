class deviceList {
  deviceName: string;
  enabled: boolean;
  valid: boolean;

  constructor(deviceName: string, enabled: boolean, valid: boolean) {
    this.deviceName = deviceName;
    this.enabled = enabled;
    this.valid = valid;
  }
}

export default deviceList;
