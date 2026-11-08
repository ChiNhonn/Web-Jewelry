import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { useToast } from './ToastContext';

interface CompareContextValue {
  slugs: string[];
  add: (slug: string, name?: string) => void;
  remove: (slug: string) => void;
  clear: () => void;
}

const CompareContext = createContext<CompareContextValue | undefined>(undefined);
const KEY = 'aura_compare';

export const CompareProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { info, error } = useToast();
  const [slugs, setSlugs] = useState<string[]>(() => {
    try {
      const raw = localStorage.getItem(KEY);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  });

  useEffect(() => {
    localStorage.setItem(KEY, JSON.stringify(slugs));
  }, [slugs]);

  const add = useCallback((slug: string, name?: string) => {
    setSlugs((prev) => {
      if (prev.includes(slug)) {
        info('Sản phẩm đã có trong danh sách so sánh.');
        return prev;
      }
      if (prev.length >= 3) {
        error('Chỉ so sánh tối đa 3 sản phẩm.', 'Đã đủ 3 món');
        return prev;
      }
      info(name ? `Đã thêm "${name}" vào so sánh.` : 'Đã thêm vào so sánh.');
      return [...prev, slug];
    });
  }, [error, info]);

  const remove = useCallback((slug: string) => {
    setSlugs((prev) => prev.filter((item) => item !== slug));
  }, []);

  const clear = useCallback(() => setSlugs([]), []);

  return (
    <CompareContext.Provider value={{ slugs, add, remove, clear }}>
      {children}
    </CompareContext.Provider>
  );
};

export const useCompare = (): CompareContextValue => {
  const context = useContext(CompareContext);
  if (!context) throw new Error('useCompare phải nằm trong CompareProvider');
  return context;
};
