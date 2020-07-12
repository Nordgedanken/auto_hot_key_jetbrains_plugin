package de.nordgedanken.auto_hotkey.sdk;

import com.google.common.flogger.FluentLogger;
import com.intellij.openapi.projectRoots.*;
import de.nordgedanken.auto_hotkey.AHKIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controls how the AutoHotkey Sdk type will look and work in the IDE. Registered in plugin.xml
 */
public final class AhkSdkType extends SdkType {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	public AhkSdkType() {
		super("AutoHotkeySDK");
	}

	@Override
	public Icon getIcon() {
		return AHKIcons.EXE;
	}

	@Override
	public @NotNull String suggestHomePath() {
		return "C:\\Program Files\\AutoHotkey";
	}

	/**
	 * Verified that there is an "AutoHotkey.exe" in the directory the user selects
	 */
	@Override
	public boolean isValidSdkHome(String selectedSdkPath) {
		try (Stream<Path> paths = Files.walk(Paths.get(selectedSdkPath), 1)) {
			return paths.filter(Files::isRegularFile)
					.anyMatch(file -> "AutoHotkey.exe".equals(file.getFileName().toString()));
		} catch (IOException e) {
			logger.atSevere().withCause(e).log();
			return false;
		}
	}

	@Override
	public String getInvalidHomeMessage(String path) {
		return "AutoHotkey.exe could not be found in the selected folder. Please ensure that AutoHotkey.exe is within the folder that you are selecting";
	}

	@Override
	public @NotNull String suggestSdkName(@Nullable String currentSdkName, String sdkHome) {
		return "AutoHotkey";
	}

	@Override
	public final String getVersionString(String sdkHome) {
		if(sdkHome == null) return null;
		final String ahkExe = new File(sdkHome, "AutoHotkey.exe").getAbsolutePath();
		try(InputStream versionScriptReader = AhkSdkType.class.getResourceAsStream("/sdk/getAhkVersion.ahk")) {
			File versionScript = File.createTempFile(String.valueOf(versionScriptReader.hashCode()), ".ahk");
			versionScript.deleteOnExit();
			Files.copy(versionScriptReader, versionScript.toPath(), StandardCopyOption.REPLACE_EXISTING);
			final String versionScriptPath = versionScript.getAbsolutePath();
			Process process = new ProcessBuilder(ahkExe, versionScriptPath).redirectErrorStream(true).start();
			boolean processCompleted = process.waitFor(3, TimeUnit.SECONDS);
			if(processCompleted) {
				String result = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
						.lines().collect(Collectors.joining("\n"));
				if(result.contains("\n")) {
					throw new IllegalStateException("The code to get the AutoHotkey version returned additional lines: " + result);
				}
				return result;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return "unknown version";
	}

	@Override
	public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
		return null;
	}

	@Override
	public @NotNull String getPresentableName() {
		return "AutoHotkey SDK";
	}

	@Override
	public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
		//do nothing for now
	}

	@Override
	public boolean setupSdkPaths(@NotNull Sdk sdk, @NotNull SdkModel sdkModel) {
		//all of this seems to do nothing right now. Have to figure it out
		return true;
	}
}
