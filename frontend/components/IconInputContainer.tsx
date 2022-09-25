import { IconContext } from "react-icons";

import styles from "../styles/IconInputContainer.module.scss";

interface Props {
  icon: React.ReactElement;
  className: string;
  children: React.ReactElement;
}

const IconInputContainer = ({ icon, className, children }: Props) => (
  <div className={`${styles.iconTextInputContainer} ${className}`}>
    <IconContext.Provider value={{ className: styles.textInputIcon }}>
      {icon}
    </IconContext.Provider>
    {children}
  </div>
);

export default IconInputContainer;