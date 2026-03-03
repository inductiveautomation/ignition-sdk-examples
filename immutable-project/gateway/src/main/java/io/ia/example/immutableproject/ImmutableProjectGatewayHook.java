package io.ia.example.immutableproject;

import java.io.IOException;
import java.io.InputStream;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.project.ProjectFileUtil;
import com.inductiveautomation.ignition.common.project.ProjectImport;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceCollectionInvalidException;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ImmutableProjectGatewayHook extends AbstractGatewayModuleHook {

    private static final LoggerEx log = LoggerEx.newBuilder().build("io.ia.example.immutableproject.ImmutableProjectGatewayHook");

    @Override
    public void setup(GatewayContext gatewayContext) {
        log.info("Setting up Immutable Project module.");
        // Stream project zip into bytes and import it using addImmutableProject method
        try (InputStream projBytes = getClass().getResourceAsStream("/sampleimmutableproject.zip")) {

            ProjectImport importedProj = ProjectFileUtil.importFromZip(projBytes, "sampleimmutableproject");
            gatewayContext.getProjectManager().addImmutableProject(importedProj);

        } catch (IOException | ResourceCollectionInvalidException e) {
            // Log error if project fails to import
            log.error("Project failed to import.", e);
        }
    }

    @Override
    public void startup(LicenseState activationState) {
        // Do nothing
    }

    @Override
    public void shutdown() {
        // Do nothing
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
