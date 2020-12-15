export const SAMPLE_DROPDOWN_CONFIG = {
	create: false,
	labelField: 'label',
	valueField: 'value',
	searchField: ['label'],
	dropdownParent : 'body'
};

export const GENES_INPUT_CONFIG = {
	create: true,
  persist: false,
	plugins: ['remove_button'],
	labelField: 'label',
	valueField: 'value',
	searchField: ['label'],
	splitOn: '\t',
  maxItems: 500
};
