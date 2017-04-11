; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "HandBook"
#define MyAppVersion "1.0"
#define MyAppPublisher "TrofimovAA"
#define MyAppExeName "handbook.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{D5AA2FB2-E2D3-4F08-BC36-B1A7BD142DB9}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
DefaultDirName={pf}\{#MyAppName}
DisableProgramGroupPage=yes
OutputDir=C:\Users\TrofimovAA\IdeaProjects\lab2
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\TrofimovAA\IdeaProjects\lab2\out\artifacts\handbook\bundles\handbook\handbook.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\TrofimovAA\IdeaProjects\lab2\out\artifacts\handbook\bundles\handbook\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKCR; SubKey: .asd; ValueType: string; ValueData: handbook; Flags: uninsdeletevalue uninsdeletekeyifempty

Root: HKCR; SubKey: handbook; Flags: uninsdeletekey
Root: HKCR; SubKey: handbook\DefaultIcon; ValueType: string; ValueData: "{app}\handbook.ico";
Root: HKCR; SubKey: handbook\shell; ValueType: string; ValueData: open;
Root: HKCR; SubKey: handbook\shell\open;
Root: HKCR; SubKey: handbook\shell\open\command; ValueType: string; ValueData: """{app}\handbook.exe"" ""%1""";