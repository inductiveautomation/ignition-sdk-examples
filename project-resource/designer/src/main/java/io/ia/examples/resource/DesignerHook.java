package io.ia.examples.resource;

import java.io.IOException;
import javax.swing.Icon;

import com.inductiveautomation.ignition.client.icons.SvgIconUtil;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.resource.ProjectResourceId;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class DesignerHook extends AbstractDesignerModuleHook {
    public static final Icon RESOURCE_ICON;

    static {
        String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(xmlParser);
        String filePath = "code-block.svg";

        try (var inputStream = DesignerHook.class.getResourceAsStream(filePath)) {
            SVGDocument document = factory.createSVGDocument(filePath, inputStream);
            RESOURCE_ICON = new SvgIconUtil.SvgIcon(document, 16, 16);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load resource icon", e);
        }
    }

    private DesignerContext context;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        this.context = context;

        PythonResourceWorkspace workspace = new PythonResourceWorkspace(context);
        context.registerResourceWorkspace(workspace);

        BundleUtil.get().addBundle("pr", DesignerHook.class, "designer");

        context.registerSearchProvider(new HandlerSearchProvider(context, workspace));
    }

    @Override
    public void shutdown() {
    }

    @Override
    public String getResourceCategoryKey(ProjectResourceId id) {
        if (id.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
            return i18n("pr.resource.category");
        } else {
            return null;
        }
    }

    @Override
    public Icon getResourceIcon(ProjectResourceId id) {
        if (id.getResourceType().equals(PythonResource.RESOURCE_TYPE)) {
            return RESOURCE_ICON;
        } else {
            return null;
        }
    }
}
