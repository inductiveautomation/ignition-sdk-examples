package org.fakester.designer.component;

import java.util.Optional;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.inductiveautomation.ignition.client.util.gui.HeaderLabel;
import com.inductiveautomation.ignition.client.util.gui.Listen;
import com.inductiveautomation.ignition.common.gson.JsonElement;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.perspective.common.api.PropertyType;
import com.inductiveautomation.perspective.common.util.PropertyJsonUtil;
import com.inductiveautomation.perspective.designer.api.ComponentDesignDelegate;
import com.inductiveautomation.perspective.designer.workspace.ComponentDetails;
import com.inductiveautomation.perspective.designer.workspace.ComponentSelection;
import com.inductiveautomation.perspective.designer.workspace.ViewResourceEditor;
import com.inductiveautomation.perspective.designer.workspace.events.SelectionPropertyUpdateEvent;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.fakester.common.component.display.TagCounter;


/**
 * Design delegates provide an opportunity to provide a user interface for configuring a component.  This interface
 * is generally a supplement to editing properties in the property tree editor provided to all Perspective Components
 * in the designer.
 *
 *
 */
public class TagCountDesignDelegate implements ComponentDesignDelegate {
    private static final LoggerEx log = LoggerEx.newBuilder().build("radComponents.TagCountDesignDelegate");

    private static final String INTERVAL_PROP_KEY = "interval";

    @Override
    public JComponent createSelectionEditor(ComponentSelection selection) {
        return new TagCountComponentConfigPanel(selection);
    }

    /**
     * Custom configuration panel that is activated/displayed when there is a single component selection.  These
     * panels are automatically subscribed to the active {@link ViewResourceEditor}'s {@link EventBus},
     * which fires a {@link SelectionPropertyUpdateEvent} when changes to the selected component's props occur.  This
     * provides a convenient mechanism for keeping UI up to date with changes to the props that might originate from
     * direct edits in the PropertyTree editor UI, bindings, etc.
     *
     * To subscribe to these events simply implement a method that takes a single {@link SelectionPropertyUpdateEvent}
     * parameter, and is annotated with {@link Subscribe} as demonstrated below.
     *
     * These delegates exist only for the duration of the current selection, and are automatically unsubscribed from the
     * EventBus when the UI is removed upon a change in selection.
     *
     * In this simple example, we add a supplementary field for configuring the 'interval' property of our TagCount
     * component.
     */
    private static class TagCountComponentConfigPanel extends JPanel  {
        ComponentSelection selection;
        JLabel label = new JLabel(StringUtils.capitalize(INTERVAL_PROP_KEY));
        JTextField textField;


        private TagCountComponentConfigPanel(ComponentSelection selection) {
            super(new MigLayout());
            this.selection = selection;

            init();
        }

        private void init() {
            textField = new JTextField(8);
            add(HeaderLabel.forKey("radcomponents.Component.TagCounter.UiDelegate.Header"), "wrap r");
            add(label, "sg a");
            add(textField, "sg a, growx, wrap");

            Long initialIntervalValue = intervalValueFromProps();
            if (initialIntervalValue != null) {
                textField.setText(initialIntervalValue.toString());
            }

            Listen.toDocumentChange(textField, this::updateIntervalPropValue);
        }


        /**
         * Does some basic checks and returns a JsonObject containing a pure property tree, without the qualified value
         * encoding.
         * @return unencoded props in an Optional (if valid and present), otherwise empty Optional
         */
        private Optional<JsonObject> getUnencodedProps() {
            // make sure the selection is just our component
            if (selection.isComponents() && 1 == selection.getComponentDetails().size()) {
                ComponentDetails details = selection.getComponentDetails().get(0);

                // make sure selected component is the type we care about
                if (TagCounter.COMPONENT_ID.equals(details.componentType)) {
                    return Optional.of(details.getUnencodedProps());
                }
            }

            return Optional.empty();
        }


        /**
         * Reads 'props.interval' value from the component's prop tree
         * @return interval value currently in props, or null if missing or invalid value.
         */
        @Nullable
        private Long intervalValueFromProps() {
            Long interval = null;

            Optional<JsonObject> maybeProps = getUnencodedProps();

            if (maybeProps.isPresent()) {
                JsonObject props = maybeProps.get();
                JsonElement propValue = props.get(INTERVAL_PROP_KEY);

                if (propValue != null) {
                    if (propValue.isJsonPrimitive()) {
                        // support both Strings that parse to Longs, as well as raw Number values
                        try {
                            if (propValue.getAsJsonPrimitive().isNumber()) {
                                interval = propValue.getAsJsonPrimitive().getAsLong();
                            }
                        } catch (NumberFormatException e) {
                            log.debugf("Failed reading property value from 'interval' property of TagCounter,"
                                       + " '%s' is not parsable to Long", propValue.toString());
                        }
                    } else {
                        log.debugf("TagCount 'interval' must be a number, or String representing a number.");
                    }
                }
            }

            return interval;
        }

        private void updateIntervalPropValue(String text) {
            selection.write(PropertyType.props, INTERVAL_PROP_KEY, text);
        }


        /**
         * Subscribe to change events for the current selection.  This event fires when there are changes/updates to the
         * property values of the current selection.  We don't know where changes originated from, just that they
         * occurred.
         */
        @Subscribe
        public void onComponentPropChange(SelectionPropertyUpdateEvent event) {
            //
            JsonElement changes = PropertyJsonUtil.decodeQualifiedValueObject(event.changes);
            if (changes != null && changes.isJsonObject()) {

                JsonObject json = changes.getAsJsonObject();

                // anchor is the first selected element.  But this config UI only loads for single component selection
                // so it's always what we care about.
                String pathOfAnchorComponent = selection.getAnchorPath().orElse(null);
                if (null != pathOfAnchorComponent && json.get(pathOfAnchorComponent) != null) {
                    if (json.get(pathOfAnchorComponent) != null && json.get(pathOfAnchorComponent).isJsonObject()) {
                        JsonObject propScopes = json.get(pathOfAnchorComponent).getAsJsonObject();
                        JsonElement props = propScopes.get("props");
                        if (props != null && props.isJsonObject()) {
                            JsonElement interval = propScopes.get("props").getAsJsonObject().get("interval");
                            if (interval != null) {
                                if (!this.textField.getText().equals(interval.toString()))
                                this.textField.setText(interval.toString());
                            }
                        }
                    }
                }
            }
        }
    }
}
