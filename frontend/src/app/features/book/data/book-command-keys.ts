export const bookCommandKeys = {
  all: () => ['books', 'command'] as const,
  readStatus: () => [...bookCommandKeys.all(), 'read-status'] as const,
  deleteBooks: () => [...bookCommandKeys.all(), 'delete'] as const,
  resetProgress: () => [...bookCommandKeys.all(), 'reset-progress'] as const,
  metadataAllLocks: () => [...bookCommandKeys.all(), 'metadata', 'all-locks'] as const,
  executeAction: () => [...bookCommandKeys.all(), 'action'] as const,
};

export const bookCommandScopes = {
  readingState: {id: 'books.command.reading-state'} as const,
  deletion: {id: 'books.command.deletion'} as const,
  metadata: {id: 'books.command.metadata'} as const,
  actions: {id: 'books.command.actions'} as const,
};
