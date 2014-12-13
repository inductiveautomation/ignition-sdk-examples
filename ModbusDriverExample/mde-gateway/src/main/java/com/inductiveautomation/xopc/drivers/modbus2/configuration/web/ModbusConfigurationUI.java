/*******************************************************************************
 * INDUCTIVE AUTOMATION PUBLIC LICENSE 
 *
 * BY DOWNLOADING, INSTALLING AND/OR IMPLEMENTING THIS SOFTWARE YOU AGREE 
 * TO THE FOLLOWING LICENSE: 
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are 
 * met: 
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions in 
 * binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. Neither the name of 
 * Inductive Automation nor the names of its contributors may be used to 
 * endorse or promote products derived from this software without specific 
 * prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL INDUCTIVE 
 * AUTOMATION BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 *
 * LICENSEE SHALL INDEMNIFY, DEFEND AND HOLD HARMLESS INDUCTIVE AUTOMATION, 
 * ITS SHAREHOLDERS, OFFICERS, DIRECTORS, EMPLOYEES, AGENTS, ATTORNEYS, 
 * SUCCESSORS AND ASSIGNS FROM ANY AND ALL claims, debts, liabilities, 
 * demands, suits and causes of action, known or unknown, in any way 
 * relating to the LICENSEE'S USE OF THE SOFTWARE IN ANY FORM OR MANNER
 * WHATSOEVER AND FOR any act or omission related thereto.
 ******************************************************************************/
package com.inductiveautomation.xopc.drivers.modbus2.configuration.web;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.web.components.ConfigPanel;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.models.RecordModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import com.inductiveautomation.xopc.driver.api.configuration.links.ConfigurationUILink;
import com.inductiveautomation.xopc.driver.api.configuration.links.ConfigurationUILink.Callback;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.AddressType;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.DesignatorRange;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.ModbusCsvParser;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.MutableModbusAddressMap;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.MutableModbusAddressMap.MutableDesignatorRange;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.map.MutableModbusAddressMap.MutableModbusRange;
import com.inductiveautomation.xopc.drivers.modbus2.configuration.settings.ModbusDriverSettings;

public class ModbusConfigurationUI<T extends PersistentRecord & ModbusDriverSettings> extends ConfigPanel {

	private static final List<AddressType> modbusTypes = AddressType.getOptions();

	private int radix = 10;
	private MutableModbusAddressMap addressMap;
	private List<ModbusConfigurationEntry> entries = new ArrayList<ModbusConfigurationEntry>();
	private ListEditor listview;

	private final IConfigPage configPage;
	private final ConfigPanel returnPanel;
	private final Callback callback;

	public ModbusConfigurationUI(
			IConfigPage configPage,
			ConfigPanel returnPanel,
			Callback callback,
			T settingsRecord) {

		this.configPage = configPage;
		this.returnPanel = returnPanel;
		this.callback = callback;

		setDefaultModel(new RecordModel<PersistentRecord>(settingsRecord));

		addComponents();
	}

	@Override
	public String[] getMenuPath() {
		return new String[0];
	}

	private void addComponents() {
		T settingsRecord = (T) getDefaultModelObject();
		String mapString = (String) TypeUtilities.toString(settingsRecord.getAddressMap());
		addressMap = MutableModbusAddressMap.fromParseableString(mapString);

		if (addressMap == null) {
			addressMap = new MutableModbusAddressMap();
		}

		radix = addressMap.getDesignatorRadix();

		final Form<Object> form = new Form<Object>("form") {
			@Override
			protected void onSubmit() {
				handleOnSubmit();
			}
		};

		form.add(new FeedbackPanel("feedback"));

		final WebMarkupContainer tableContainer = new WebMarkupContainer("table-container");
		tableContainer.setOutputMarkupId(true);

		final WebMarkupContainer radixContainer = new WebMarkupContainer("radix-container") {
			@Override
			public boolean isVisible() {
				return entries.size() > 0;
			}
		};

		radixContainer.setOutputMarkupId(true);

		radixContainer.add(new Label("radix-label", new LenientResourceModel("radixlabel", "Radix")) {
			@Override
			public boolean isVisible() {
				return entries.size() > 0;
			}
		});

		final RequiredTextField<Integer> radixField = new RequiredTextField<Integer>("radix",
				new PropertyModel<Integer>(this, "radix")) {
			@Override
			public boolean isVisible() {
				return entries.size() > 0;
			}
		};

		radixField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					Integer radix = Integer.parseInt(radixField.getValue());
					setRadix(radix);
				} catch (Exception e) {
				}
			}
		});

		radixContainer.add(radixField);

		tableContainer.add(radixContainer);

		radixContainer.add(new Link<Object>("set-radix") {
			@Override
			public void onClick() {
				if (addressMap != null) {

				}
			}

			@Override
			public boolean isVisible() {
				return false;
			}
		});

		// Create the configuration entries for the listview
		for (DesignatorRange dr : addressMap.keySet()) {
			MutableDesignatorRange mdr = new MutableDesignatorRange(dr);
			MutableModbusRange mbr = new MutableModbusRange(addressMap.get(dr));
			entries.add(new ModbusConfigurationEntry(mdr, mbr));
		}

		// Create the listview
		listview = new ListEditor<ModbusConfigurationEntry>("config-listview", getListviewModel()) {

			@Override
			protected void onPopulateItem(ListItem<ModbusConfigurationEntry> item) {
				final ModbusConfigurationEntry configEntry = item.getModelObject();

				item.add(newPrefixTextField(configEntry));

				item.add(newStartTextField(configEntry));

				item.add(newEndTextField(configEntry));

				item.add(newStepCheckboxField(configEntry));

				item.add(newModbusUnitIDTextField(configEntry));

				item.add(newModbusAddressTypeDropdown(configEntry));

				item.add(newModbusAddressTextField(configEntry));

				item.add(new DeleteLink("delete-link"));
			}
		};

		WebMarkupContainer noMappingsContainer = new WebMarkupContainer("no-mappings-container") {
			@Override
			public boolean isVisible() {
				return entries.size() == 0;
			}

			;
		};
		noMappingsContainer.add(new Label("no-mappings-label", new LenientResourceModel("nomappings",
				"No mappings.")));

		tableContainer.add(noMappingsContainer);

		tableContainer.add(listview);
		form.add(tableContainer);

		form.add(new SubmitLink("add-row-link") {
			{
				setDefaultFormProcessing(false);
			}

			@Override
			public void onSubmit() {
				listview.addItem(new ModbusConfigurationEntry());
			}
		});

		form.add(new Button("save"));

		add(form);

		// CSV export
		try {
			Link<IResource> exportLink = new ResourceLink<IResource>("export-link", new ExportCsvResource());
			add(exportLink);
		} catch (Exception e) {
			Link<Object> exportLink = new Link<Object>("export-link") {
				@Override
				public void onClick() {
				}

				@Override
				public boolean isVisible() {
					return false;
				}
			};
			add(exportLink);
		}

		// CSV import
		final FileUploadField uploadField = new FileUploadField("upload-field");

		Form<?> uploadForm = new Form<Object>("upload-form") {
			@Override
			protected void onSubmit() {
				try {
					addressMap = ModbusCsvParser.fromCsv(uploadField.getFileUpload().getInputStream());

					radix = addressMap.getDesignatorRadix();

					listview.clear();

					for (DesignatorRange dr : addressMap.keySet()) {
						MutableDesignatorRange mdr = new MutableDesignatorRange(dr);
						MutableModbusRange mbr = new MutableModbusRange(addressMap.get(dr));
						listview.addItem(new ModbusConfigurationEntry(mdr, mbr));
					}

				} catch (Exception e) {
					error("Error importing configuration from CSV file.");
				}
			}
		};

		uploadForm.add(uploadField);

		SubmitLink importLink = new SubmitLink("import-link");

		uploadForm.add(importLink);

		add(uploadForm);

	}

	private IModel<List<ModbusConfigurationEntry>> getListviewModel() {
		return new LoadableDetachableModel<List<ModbusConfigurationEntry>>() {
			@Override
			protected List<ModbusConfigurationEntry> load() {
				return entries;
			}
		};
	}

	private TextField<String> newPrefixTextField(final ModbusConfigurationEntry configEntry) {
		final RequiredTextField<String> textField = new RequiredTextField<String>("prefix",
				new PropertyModel<String>(configEntry, "designatorRange.designator"));

		textField.add(new PatternValidator("[A-Za-z0-9_]+") {
			@Override
			protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
				error.addKey("prefix.PatternValidator");
				return error;
			}
		});

		textField.add(new StringValidator(1, 12) {
			@Override
			protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
				error.addKey("prefix.LengthValidator");
				return error;
			}
		});

		textField.add(new PrefixValidator());

		textField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.getDesignatorRange().setDesignator(textField.getModelObject());
			}
		});

		return textField;
	}

	static class PrefixValidator extends StringValidator {
		@Override
		public void validate(IValidatable<String> validatable) {
			final String value = validatable.getValue();

			if (StringUtils.equalsIgnoreCase("HR", value) ||
					StringUtils.equalsIgnoreCase("IR", value) ||
					StringUtils.equalsIgnoreCase("C", value) ||
					StringUtils.equalsIgnoreCase("DI", value)) {
				class PrefixError implements IValidationError, Serializable {
					@Override
					public String getErrorMessage(IErrorMessageSource arg0) {
						return String.format("Prefix \"%s\" not allowed.", value);
					}
				}
				validatable.error(new PrefixError());
			}
		}
	}

	private TextField<String> newStartTextField(final ModbusConfigurationEntry configEntry) {
		final RequiredTextField<String> textField = new RequiredTextField<String>("start",
				new PropertyModel<String>(configEntry, "designatorRange.start"));

		textField.add(new RadixValidator());

		textField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.getDesignatorRange().setStart(textField.getModelObject());
			}
		});

		return textField;
	}

	private TextField<String> newEndTextField(final ModbusConfigurationEntry configEntry) {
		final RequiredTextField<String> textField = new RequiredTextField<String>("end",
				new PropertyModel<String>(configEntry, "designatorRange.end"));

		textField.add(new RadixValidator());

		textField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.getDesignatorRange().setEnd(textField.getModelObject());
			}
		});

		return textField;
	}

	private CheckBox newStepCheckboxField(final ModbusConfigurationEntry configEntry) {
		final CheckBox checkboxField = new CheckBox("step",
				new PropertyModel<Boolean>(configEntry, "designatorRange.step"));

		checkboxField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.getDesignatorRange().setStep(checkboxField.getModelObject());
			}
		});

		return checkboxField;
	}

	private TextField<String> newModbusUnitIDTextField(final ModbusConfigurationEntry configEntry) {
		final RequiredTextField<String> textField = new RequiredTextField<String>("unitid",
				new PropertyModel<String>(configEntry, "modbusRange.unitID"));

		textField.add(new ModbusUnitIDValidator());

		textField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.modbusRange.setUnitID(textField.getModelObject());
			}
		});

		return textField;
	}

	private TextField<String> newModbusAddressTextField(final ModbusConfigurationEntry configEntry) {
		final RequiredTextField<String> textField = new RequiredTextField<String>("address",
				new PropertyModel<String>(configEntry, "modbusRange.start"));

		textField.add(new ModbusAddressValidator());

		textField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.modbusRange.setStart(textField.getModelObject());
			}
		});

		return textField;
	}

	private DropDownChoice<AddressType> newModbusAddressTypeDropdown(
			final ModbusConfigurationEntry configEntry) {
		final DropDownChoice<AddressType> dropDown = new DropDownChoice<AddressType>(
				"type", new PropertyModel<AddressType>(configEntry,
						"modbusRange.modbusAddressType"), modbusTypes,
				new ModbusAddressTypeChoiceRenderer());

		dropDown.setRequired(true);

		dropDown.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				configEntry.getModbusRange().setModbusAddressType(dropDown.getModelObject());
			}
		});

		return dropDown;
	}

	public int getRadix() {
		return radix;
	}

	public void setRadix(int radix) {
		this.radix = radix;
	}

	private void handleOnSubmit() {
		MutableModbusAddressMap addressMap = new MutableModbusAddressMap();
		entries = (List<ModbusConfigurationEntry>) listview.getDefaultModelObject();

		for (ModbusConfigurationEntry entry : entries) {
			MutableDesignatorRange dr = entry.getDesignatorRange();
			MutableModbusRange mr = entry.getModbusRange();

			addressMap.put(dr, mr);
		}

		addressMap.setDesignatorRadix(radix);

		T settingsRecord = (T) getDefaultModelObject();
		settingsRecord.setAddressMap(addressMap.toParseableString());
		callback.save(settingsRecord);
		configPage.setConfigPanel(returnPanel);
	}

	@Override
	public IModel<String> getTitleModel() {
		return new LenientResourceModel("configurationtitle", "Address Configuration");
	}

	private class ModbusAddressTypeChoiceRenderer implements IChoiceRenderer<AddressType> {
		@Override
		public Object getDisplayValue(AddressType type) {
			return type.getDisplayString();
		}

		@Override
		public String getIdValue(AddressType type, int index) {
			return String.valueOf(index);
		}
	}

	public static class ModbusConfigurationEntry implements Serializable {

		private MutableDesignatorRange designatorRange;
		private MutableModbusRange modbusRange;

		public ModbusConfigurationEntry() {
			designatorRange = new MutableDesignatorRange();
			modbusRange = new MutableModbusRange();
		}

		public ModbusConfigurationEntry(MutableDesignatorRange designatorRange,
				MutableModbusRange modbusRange) {
			this.designatorRange = designatorRange;
			this.modbusRange = modbusRange;
		}

		public MutableDesignatorRange getDesignatorRange() {
			return designatorRange;
		}

		public MutableModbusRange getModbusRange() {
			return modbusRange;
		}

	}

	private class RadixValidator implements IValidator<String>, Serializable {

		@Override
		public void validate(IValidatable<String> validatable) {
			String value = validatable.getValue();

			try {
				Integer.parseInt(value, radix);
			} catch (Exception e) {
				validatable.error(new RadixError(value, radix));
			}
		}

		private class RadixError implements IValidationError, Serializable {
			private String value;
			private int base;

			public RadixError(String value, int base) {
				this.value = value;
				this.base = base;
			}

			@Override
			public String getErrorMessage(IErrorMessageSource source) {
				return String.format("Value \"%s\" outside of specified number base \"%s\".", value,
						base);
			}
		}
	}

	private class ModbusUnitIDValidator implements IValidator<String>, Serializable {

		@Override
		public void validate(IValidatable<String> validatable) {
			String value = validatable.getValue();
			try {
				int unitID = Integer.parseInt(value);
				if (unitID < 0 || unitID > 255) {
					validatable.error(new ModbusUnitIDError());
				}
			} catch (Exception e) {
				validatable.error(new ModbusUnitIDError());
			}
		}

		private class ModbusUnitIDError implements IValidationError, Serializable {
			@Override
			public String getErrorMessage(IErrorMessageSource arg0) {
				return "Modbus Unit ID must be blank or a number between 0 and 255";
			}
		}

	}

	private class ModbusAddressValidator implements IValidator<String>, Serializable {

		@Override
		public void validate(IValidatable<String> validatable) {
			String value = validatable.getValue();
			try {
				int address = Integer.parseInt(value);
				if (address < 0 || address > 65535) {
					validatable.error(new ModbusError());
				}
			} catch (Exception e) {
				validatable.error(new ModbusError());
			}
		}

		private class ModbusError implements IValidationError, Serializable {
			@Override
			public String getErrorMessage(IErrorMessageSource arg0) {
				return "Modbus Address must be between 0 and 65535";
			}
		}

	}

	private class ExportCsvResource extends ByteArrayResource {

		private byte[] resource;

		ExportCsvResource() {
			super("text/plain", null, "modbus-config.csv");
		}

		@Override
		protected byte[] getData(Attributes attributes) {
			MutableModbusAddressMap addressMap = new MutableModbusAddressMap();

			for (ModbusConfigurationEntry entry : entries) {
				MutableDesignatorRange dr = entry.getDesignatorRange();
				MutableModbusRange mr = entry.getModbusRange();

				addressMap.put(dr, mr);
			}

			addressMap.setDesignatorRadix(radix);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			try {
				addressMap.toCsv(baos);
			} catch (Exception e) {
			}

			resource = baos.toByteArray();

			IOUtils.closeQuietly(baos);
			return resource;
		}

	}

	public abstract class ListEditor<T> extends RepeatingView implements IFormModelUpdateListener {
		List<T> items;

		public ListEditor(String id, IModel<List<T>> model) {
			super(id, model);
		}

		protected abstract void onPopulateItem(ListItem<T> item);

		public void addItem(T value) {
			items.add(value);
			ListItem<T> item = new ListItem<T>(newChildId(),
					items.size() - 1);
			add(item);
			onPopulateItem(item);
		}

		public void clear() {
			items.clear();
			removeAll();
		}

		protected void onBeforeRender() {
			if (!hasBeenRendered()) {
				items = new ArrayList<T>((List<T>) getDefaultModelObject());
				for (int i = 0; i < items.size(); i++) {
					ListItem<T> li = new ListItem<T>(newChildId(), i);
					add(li);
					onPopulateItem(li);
				}
			}
			super.onBeforeRender();
		}

		public void updateModel() {
			setDefaultModelObject(items);
		}
	}

	public class ListItem<T> extends Item<T> {
		public ListItem(String id, int index) {
			super(id, index);
			setModel(new ListItemModel());
		}

		private class ListItemModel extends AbstractReadOnlyModel<T> {
			public T getObject() {
				return ((ListEditor<T>) ListItem.this.getParent()).items.get(getIndex());
			}
		}
	}

	public abstract class EditorLink extends SubmitLink {
		private transient ListItem<?> parent;

		public EditorLink(String id) {
			super(id);
		}

		protected final ListItem<?> getItem() {
			if (parent == null) {
				parent = findParent(ListItem.class);
			}
			return parent;
		}

		protected final List<?> getList() {
			return getEditor().items;
		}

		protected final ListEditor<?> getEditor() {
			return (ListEditor<?>) getItem().getParent();
		}

		protected void onDetach() {
			parent = null;
			super.onDetach();
		}

	}

	public class DeleteLink extends EditorLink {

		public DeleteLink(String id) {
			super(id);
			setDefaultFormProcessing(false);
		}

		@Override
		public void onSubmit() {
			int idx = getItem().getIndex();

			for (int i = idx + 1; i < getItem().getParent().size(); i++) {
				ListItem<?> item = (ListItem<?>) getItem().getParent().get(i);
				item.setIndex(item.getIndex() - 1);
			}

			getList().remove(idx);
			getEditor().remove(getItem());
		}
	}

}
