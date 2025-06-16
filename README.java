import React, { useState } from 'react';
import { Plus, Trash2, Calculator, Download } from 'lucide-react';

export default function ResourceCalculator() {
  const [resources, setResources] = useState([
    { id: 1, name: 'خام الذهب', quantity: 0, pricePerUnit: 0, totalPrice: 0, divideBy: 1, dividedQuantity: 0, dividedPrice: 0 }
  ]);

  const addResource = () => {
    const newResource = {
      id: Date.now(),
      name: '',
      quantity: 0,
      pricePerUnit: 0,
      totalPrice: 0,
      divideBy: 1,
      dividedQuantity: 0,
      dividedPrice: 0
    };
    setResources([...resources, newResource]);
  };

  const removeResource = (id) => {
    setResources(resources.filter(resource => resource.id !== id));
  };

  const updateResource = (id, field, value) => {
    setResources(resources.map(resource => {
      if (resource.id === id) {
        const updatedResource = { ...resource, [field]: value };
        
        // Calculate total price when quantity or price changes
        if (field === 'quantity' || field === 'pricePerUnit') {
          updatedResource.totalPrice = updatedResource.quantity * updatedResource.pricePerUnit;
        }
        
        // Calculate division results
        if (field === 'quantity' || field === 'pricePerUnit' || field === 'divideBy') {
          const divideBy = field === 'divideBy' ? value : updatedResource.divideBy;
          if (divideBy > 0) {
            updatedResource.dividedQuantity = updatedResource.quantity / divideBy;
            updatedResource.dividedPrice = updatedResource.totalPrice / divideBy;
          } else {
            updatedResource.dividedQuantity = 0;
            updatedResource.dividedPrice = 0;
          }
        }
        
        return updatedResource;
      }
      return resource;
    }));
  };

  const calculateTotal = () => {
    return resources.reduce((total, resource) => total + resource.totalPrice, 0);
  };

  const formatNumber = (num) => {
    return num.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  const formatPriceNumber = (num) => {
    // Remove trailing zeros after decimal point
    return num.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
  };

  const exportToCSV = () => {
    const headers = [
      'اسم المورد',
      'الكمية',
      'السعر لكل وحدة',
      'السعر الإجمالي',
      'القسمة على',
      'الكمية لكل قسمة',
      'السعر لكل قسمة'
    ];

    const csvData = resources.map(resource => [
      resource.name || 'غير محدد',
      resource.quantity,
      resource.pricePerUnit,
      resource.totalPrice,
      resource.divideBy,
      resource.dividedQuantity.toFixed(2),
      resource.dividedPrice.toFixed(2)
    ]);

    // Add summary row
    const summaryRow = [
      'الملخص الإجمالي',
      calculateTotalQuantity(),
      '',
      calculateTotal(),
      '',
      '',
      ''
    ];

    const csvContent = [
      headers.join(','),
      ...csvData.map(row => row.join(',')),
      summaryRow.join(',')
    ].join('\n');

    // Create and download file
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `موارد_${new Date().toLocaleDateString('ar-SA')}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const calculateTotalQuantity = () => {
    return resources.reduce((total, resource) => total + Number(resource.quantity), 0);
  };

  return (
    <div className="min-h-screen bg-gray-800 p-6" dir="rtl">
      <div className="max-w-6xl mx-auto">
        <div className="bg-gray-700 rounded-lg shadow-lg p-8">
          <div className="flex items-center justify-center mb-8">
            <Calculator className="w-8 h-8 text-gray-400 ml-3" />
            <h1 className="text-3xl font-bold text-gray-100">حاسبة الموارد</h1>
          </div>

          <div className="space-y-6">
            {resources.map((resource, index) => (
              <div key={resource.id} className="bg-gray-600 rounded-lg p-6 border border-gray-500">
                <div className="flex items-center justify-between mb-4">
                  <h3 className="text-lg font-semibold text-gray-100">مورد رقم {index + 1}</h3>
                  {resources.length > 1 && (
                    <button
                      onClick={() => removeResource(resource.id)}
                      className="text-red-400 hover:text-red-300 transition-colors p-2"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      اسم المورد
                    </label>
                    <input
                      type="text"
                      value={resource.name}
                      onChange={(e) => updateResource(resource.id, 'name', e.target.value)}
                      placeholder="أدخل اسم المورد"
                      className="w-full px-4 py-3 bg-gray-500 border border-gray-400 rounded-lg text-gray-100 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      الكمية
                    </label>
                    <input
                      type="number"
                      value={resource.quantity}
                      onChange={(e) => updateResource(resource.id, 'quantity', Number(e.target.value) || 0)}
                      placeholder="0"
                      className="w-full px-4 py-3 bg-gray-500 border border-gray-400 rounded-lg text-gray-100 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      السعر لكل وحدة (كوين)
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      value={resource.pricePerUnit}
                      onChange={(e) => updateResource(resource.id, 'pricePerUnit', Number(e.target.value) || 0)}
                      placeholder="0.00"
                      className="w-full px-4 py-3 bg-gray-500 border border-gray-400 rounded-lg text-gray-100 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      القسمة على
                    </label>
                    <input
                      type="number"
                      value={resource.divideBy}
                      onChange={(e) => updateResource(resource.id, 'divideBy', Number(e.target.value) || 1)}
                      placeholder="1"
                      min="1"
                      className="w-full px-4 py-3 bg-gray-500 border border-gray-400 rounded-lg text-gray-100 placeholder-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      السعر الإجمالي
                    </label>
                    <div className="w-full px-4 py-3 bg-gray-500 border border-gray-400 rounded-lg text-gray-100 font-semibold">
                      {formatPriceNumber(resource.totalPrice)} كوين
                    </div>
                  </div>
                </div>

                <div className="mt-4 p-4 bg-gray-650 rounded-lg">
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4 text-sm">
                    <div className="text-gray-300">
                      <span className="font-medium">المورد:</span> {resource.name || 'غير محدد'}
                    </div>
                    <div className="text-gray-300">
                      <span className="font-medium">إجمالي العناصر:</span> {resource.quantity}
                      {resource.divideBy > 1 && (
                        <div className="text-gray-400 text-xs mt-1">
                          <span className="font-medium">الكمية لكل قسمة:</span> {formatNumber(resource.dividedQuantity)}
                        </div>
                      )}
                    </div>
                    <div className="text-gray-300">
                      <span className="font-medium">التكلفة الفردية:</span> {formatPriceNumber(resource.pricePerUnit)} كوين
                    </div>
                    <div className="text-gray-300">
                      <span className="font-medium">عدد الأقسام:</span> {resource.divideBy}
                    </div>
                  </div>
                </div>
              </div>
            ))}

            <div className="flex gap-4">
              <button
                onClick={addResource}
                className="flex-1 py-4 bg-gray-600 hover:bg-gray-500 text-gray-100 font-semibold rounded-lg transition-all duration-300 flex items-center justify-center space-x-2 border border-gray-500"
              >
                <Plus className="w-5 h-5" />
                <span>إضافة مورد جديد</span>
              </button>

              <button
                onClick={exportToCSV}
                className="py-4 px-6 bg-green-600 hover:bg-green-500 text-white font-semibold rounded-lg transition-all duration-300 flex items-center justify-center space-x-2 border border-green-500"
              >
                <Download className="w-5 h-5" />
                <span>تصدير CSV</span>
              </button>
            </div>

            <div className="bg-gray-600 rounded-lg p-6 border border-gray-500">
              <h2 className="text-2xl font-bold text-gray-100 mb-4 text-center">الملخص</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="text-center">
                  <div className="text-3xl font-bold text-gray-100">{resources.length}</div>
                  <div className="text-gray-300">إجمالي الموارد</div>
                </div>
                <div className="text-center">
                  <div className="text-3xl font-bold text-gray-100">{calculateTotalQuantity()}</div>
                  <div className="text-gray-300">إجمالي العناصر</div>
                </div>
                <div className="text-center">
                  <div className="text-3xl font-bold text-gray-100">{formatPriceNumber(calculateTotal())}</div>
                  <div className="text-gray-300">التكلفة الإجمالية (كوين)</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
