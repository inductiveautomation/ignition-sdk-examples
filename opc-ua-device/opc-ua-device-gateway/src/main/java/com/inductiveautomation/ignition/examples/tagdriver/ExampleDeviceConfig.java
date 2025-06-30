package com.inductiveautomation.ignition.examples.tagdriver;

import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormCategory;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;

public record ExampleDeviceConfig(General general) {

  record General(
      @FormCategory("GENERAL")
          @Label("Tag Count*")
          @FormField(FormFieldType.NUMBER)
          @DefaultValue("10")
          @Required
          @Description("Number of tags.")
          int tagCount) {}
}
