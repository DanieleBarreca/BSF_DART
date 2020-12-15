//https://github.com/galkinrost/selectize.js/tree/infinite_scroll/src/plugins/infinite_scroll

/**
 * Plugin: "infinite_scroll" (selectize.js)
 * Copyright (c) 2013 Simon Hewitt & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * @author Galkin Rostislav <galkinrost@gmail.com>
 */

Selectize.define('infinite_scroll', function (options) {
  var self = this;
  var deleteSelection = self.deleteSelection;
  var addItem = self.addItem;

  options = $.extend({
    loadingOffset: 50
  }, options);

  $.extend(self, {
    loadedPages: {},
  });

  self.onPageChange = function () {
    var fn = self.settings.load;
    var query = self.lastQuery;
    if (!fn || self.loadedPages[query] === false) return;
    var page = self.loadedPages[query] = (self.loadedPages[query] || 1) + 1;
    self.load(function (callback) {
      function middleware(res) {
        if (!res || res.length === 0) {
          (function (query) {
            self.loadedPages[query] = false;
          })(query);
        }
        callback.apply(self, [res]);
      }
      fn.apply(self, [query, page, middleware]);
    });
  };

  self.deleteSelection = function (e) {
    deleteSelection.apply(self, [e]);
    self.loadedPages = {};
    self.onSearchChange('');
  };

  self.onSearchChange = function (value ) {
    var fn = self.settings.load;
    if (!fn) return;
    if (self.loadedPages.hasOwnProperty(value)) return;
    self.clearOptions();
    self.loadedPages = {};
    self.loadedSearches[value] = true;
    self.loadedPages[value] = 1;
    self.load(function (callback) {
      fn.apply(self, [value, 1, callback]);
    });
  };

  self.onFocus = function(e) {
    var self = this;
    var wasFocused = self.isFocused;

    if (self.isDisabled) {
      self.blur();
      e && e.preventDefault();
      return false;
    }

    if (self.ignoreFocus) return;
    self.isFocused = true;

    if (!wasFocused) {

      if (self.settings.preload === 'focus'){
        self.loadedPages = {};
        var searchValue='';
        if (self.items && self.items.length>0) {
          searchValue = self.options[+self.items[0]].LABEL;
        }
        self.clearOptions();
        self.onSearchChange(searchValue);
      }
      self.trigger('focus');
    }

    if (!self.$activeItems.length) {
      self.showInput();
      self.setActiveItem(null);
      self.refreshOptions(!!self.settings.openOnFocus);
    }

    self.refreshState();
  };

  self.on('load',function() {
    self.$dropdown_content.scroll(function () {
      if (self.loading) return;
      if ($(this)[0].scrollHeight - ($(this).scrollTop() + $(this).height()) < options.loadingOffset) {
        self.onPageChange();
      }
    })
  });

});
