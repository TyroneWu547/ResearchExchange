import { useState } from "react";
import { FaPlus, FaTimes } from "react-icons/fa";

import styles from "../styles/MultiSelection.module.scss";

interface Props {
  values: string[];
  setValues: (values: string[]) => void;
  name: string;
  style: "filters" | "post-article";
}

export default function MultiSelection({ values, setValues, name, style }: Props) {
  const [text, setText] = useState("");

  function handleAddValue() {
    setValues([...values, text]);
    setText("");
  }

  const containerClassName = {
    "filters": styles.multipleSelectionFilters,
    "post-article": styles.multipleSelectionPostArticle
  }[style];

  return (
    <div id={`${name.replace(' ', '-')}-container`} className={containerClassName}>
      {values.length > 0 ? (
        <ul className={styles.selectedItems}>
          {values.map(x => <li key={x} onClick={() => setValues(values.filter(e => e !== x))}>{x} <FaTimes /></li>)}
        </ul>
      ) : (
        <p className={styles.noSelected}>No {name}s added</p>
      )}

      <div className={`${styles.postTextField} ${styles.textWithButton}`}>
        <input type="text" placeholder={"Enter one " + name + " at a time and press the + button to add it."} value={text} onChange={e => setText(e.target.value)} />
        <button type="button" disabled={text === "" || values.includes(text)} onClick={handleAddValue}><FaPlus /></button>
      </div>
    </div>
  );
}