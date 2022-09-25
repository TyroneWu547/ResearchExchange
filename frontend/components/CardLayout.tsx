import styles from "../styles/CardLayout.module.scss";

interface Props {
  width: string | number;
  children: React.ReactNode;
}

export default function CardLayout({ width, children }: Props) {
  return (
    <main className={styles.card} style={{ width }}>
      {children}
    </main>
  );
}