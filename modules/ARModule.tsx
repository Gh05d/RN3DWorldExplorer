import { NativeModules } from "react-native";
const { ARModule } = NativeModules;

interface ARModuleInterface {
  showAR(path: string): Promise<void>;
}

export default ARModule as ARModuleInterface;
